/*
 *
 *
 *
 */

PartPanel = Ext.extend(Ext.Panel, {
    title: 'Part',
    layout: 'border',
    tpl: '',
    closable: true,
    autoScroll: false,
    //itemId: 'partPanel',

    //Data Members
    partRecord: null,
    parts: null,

    //Function Members
    initComponent: function() {
        this.items = [
            {
                xtype: 'panel',
                title: 'DNA Sequence',
                height: 100,
                layout: 'fit',
                ref: 'sequencePanel',
                region: 'north',
                split: true,
                items: [
                    {
                        xtype: 'textarea',
                        ref: '../sequenceTextArea',
                        readOnly: true
                    }
                ]
            },
            {
                xtype: 'panel',
                title: 'Performance',
                layout: 'auto',
                region: 'center',
                split: true,
                ref: 'performancePanel'
            },
            {
                xtype:'panel',
                title: 'Notes',
                layout: 'fit',
                height: 125,
                ref: 'notesPanel',
                region: 'south',
                split: true,
                items:[
                    {
                        xtype: 'textarea',
                        hidden: false,
                        ref: '../notesPanelTextArea'
                    }
                ]

            }
        ];

        PartPanel.superclass.initComponent.call(this);
    },
    
    displayInfo: function(partRecord, parts)
    {
            var dnaSequence = null;
            var biofabID = null;

            this.partRecord = partRecord;
            this.parts = parts;
            biofabID = partRecord.get('displayId');
            this.setTitle(biofabID);
            this.sequencePanel.setTitle('DNA Sequence of ' + biofabID);
            this.performancePanel.setTitle('Performance of ' + biofabID);
            dnaSequence = partRecord.get('dnaSequence');
            this.sequenceTextArea.setValue(dnaSequence);
            var collectionId = this.partRecord.get('collectionId');

            //Temporary patch till I fix the problem with the Pilot Project
            if(collectionId !== 1)
            {
                this.generateBarChart();
            }
            else
            {
                this.performancePanel.add(
                    {
                        xtype: 'label',
                        text: 'Performance data for Pilot Project parts will be available in an upcoming release of the Data Access Client.'
                    }
                );
                this.performancePanel.doLayout();
            }
    },

    generateBarChart: function()
    {
        var newStore;
        var partPerformances;

        Ext4.define('PartPerformance', {
            extend: 'Ext4.data.Model',
            fields: [
                {name: 'biofabId', type: 'string'},
                {name: 'value', type: 'float'}
            ]
        });

        if(this.partRecord !== null && this.parts !== null)
        {
            partPerformances = this.generatePartPerformances(this.partRecord, this.parts);

            newStore = new Ext4.data.Store({
                model: 'PartPerformance',
                data : partPerformances
            });

            var element = this.performancePanel.getEl();

            var barChart = Ext4.create('Ext.chart.Chart',
                {
                    theme: 'Category1',
                    width: 600,
                    height: 400,
                    renderTo: element.dom,
                    animate: false,
                    store: newStore,
                    axes: [
                        {
                          type: 'Numeric',
                          position: 'left',
                          fields: ['value'],
                          label: {
                              renderer: Ext.util.Format.numberRenderer('0,0'),
                              font: '11px Arial'
                          },
                          title: 'Gene Expression per Cell (AU)',
                          grid: true,
                          minimum: 0,
                          labelTitle: {font: '12px Arial'}
                        },
//                        {
//                          type: 'Category',
//                          position: 'bottom',
//                          fields: ['biofabID'],
//                          title: 'Part',
//                          minimum: 0,
//                          labelTitle: {font: '12px Arial'},
//                          label: {
//                              font: '11px Arial',
//                              rotate: {degrees: 90}
//                          },
//                          calculateCategoryCount: true
//                        }
                    ],
                    series: [
                        {
                            type: 'column',
                            axis: 'left',
                            xField: 'biofabId',
                            yField: 'value',
                            highlight: false,
                            style: {opacity: 1.0},
                            //gutter: 5,
                            tips: {
                              trackMouse: true,
                              width: 120,
                              height: 28,
                              renderer: function(storeItem, item) {
                                this.setTitle('Part: ' + storeItem.get('biofabId'));
                              }
                            },
                            renderer: function(sprite, record, attr, index, store)
                                {
                                    //var biofabId = record.get('biofabId');
                                    //var partPanel = Ext.Container.getComponent('partPanel');
                                    var isSelectedPart = record.get('isSelectedPart');

                                    if(isSelectedPart)
                                    {
                                        var color = 'rgb(255, 0, 0)';
                                        return Ext.apply(attr, {fill: color});
                                    }
                                    else
                                    {
                                        return Ext.apply(attr);
                                    }

//                                    if(index === 10)
//                                    {
//                                        var color = 'rgb(255, 0, 0)';
//                                        return Ext.apply(attr, {fill: color});
//                                    }
//                                    else
//                                    {
//                                        return Ext.apply(attr);
//                                    }
                                }

//                            label: {
//                                display: 'insideEnd',
//                                field: 'biofabId',
//                                renderer: Ext.util.Format.format,
//                                orientation: 'vertical',
//                                color: '#333',
//                               'text-anchor': 'middle'
//                            }
                        }
                    ]
                }
            );

            this.performancePanel.removeAll(true);
            this.performancePanel.add(barChart);
            this.performancePanel.doLayout();
            this.displayNotes();
        }
        else
        {
          Ext.Msg.alert('Part Performance', 'There is an error. The part performance bar chart can not be generated.');
        }
    },

    generatePartPerformances: function(partRecord, parts)
    {
        var part;
        var partCollection = [];
        var partPerformances = [];
        var partCount;
        var measurement;
        var performance;
        var measurements;
        var measurementsCount;
        var selectedMeasurements = [];
        var maxMeasurement;
        var collectionId;
        var selectedMeasurement;
        var selectedPartBiofabId;
        var isSelected;

        selectedPartBiofabId = partRecord.get('displayId')
        partCount = this.parts.length;
        collectionId = partRecord.get('collectionId');

        for(var j = 0; j < partCount; j += 1)
        {
            part = parts[j];

            if(part.collectionID === collectionId)
            {
                if(part.performance != undefined)
                {
                    measurements = part.performance.measurements;

                    if(measurements != undefined)
                    {
                        measurementsCount = measurements.length;

                        for(var i = 0; i < measurementsCount; i += 1)
                        {
                            measurement = measurements[i];
        
                            if(measurement.type === 'GEC')
                            {
                                if(part.displayID === selectedPartBiofabId)
                                {
                                    isSelected = true;
                                }
                                else
                                {
                                    isSelected = false
                                }

                                selectedMeasurements.push(
                                    {
                                        biofabId: part.displayID,
                                        isSelectedPart: isSelected ,
                                        value: measurement.value
                                    }
                                );
                            }
                        }

                        if(selectedMeasurements.length > 1)
                        {
                            selectedMeasurements.sort(
                                function(a,b)
                                {
                                    return a.value - b.value;
                                }
                            );
        
                            maxMeasurement = selectedMeasurements.pop();
                            partPerformances.push(maxMeasurement);
                        }
                        else
                        {
                            if(selectedMeasurements.length === 1)
                            {
                                partPerformances.push(selectedMeasurements[0]);
                            }
                        }
                    }
                }
            }
        }

        partPerformances.sort(
            function(a,b)
            {
                return b.value - a.value;
            }
        );
        
        return partPerformances;
    },

    displayNotes: function()
    {
        this.notesPanelTextArea.setValue('Each bar indicates the performance of a part in the library.\n' +
                               'Hover the mouse over a bar to see the identifier of a part.\n' +
                               'The red bar indicates the performance of the selected part.');
    }
});
