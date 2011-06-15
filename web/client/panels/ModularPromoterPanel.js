/*
 *
 * File: PilotProjectPanel.js
 * 
 */

ModularPromoterPanel = Ext.extend(ModularPromoterPanelUi,
{
    collectionRecord: null,
    parts: null,

    initComponent: function()
    {
        ModularPromoterPanel.superclass.initComponent.call(this);
    },

    //
    //  Public Methods
    //
    
//    setCollectionRecord: function(collectionRecord)
//    {
//            var description = null;
//
//            this.collectionRecord = collectionRecord;
//            description = collectionRecord.get('description');
//            this.collectionTextAreaRef.setValue(description);
//    },

    displayInfo: function(collectionRecord, parts)
    {
            var description = null;
            this.collectionRecord = collectionRecord;
            this.parts = parts;
            description = collectionRecord.get('description');
            this.collectionTextAreaRef.setValue(description);
            this.generateBarChart();
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

        if(this.collectionRecord !== null && this.parts !== null)
        {
            partPerformances = this.generatePartPerformances(this.collectionRecord, this.parts);

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
//                    legend:
//                        {
//                            position: 'bottom'
//                        },
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
                        }
//                        {
//                          type: 'Category',
//                          position: 'bottom',
//                          fields: ['biofabId'],
//                          title: 'Modular Promoters',
//                          minimum: 0,
//                          labelTitle: {font: '12px Arial'},
//                          label: {display: 'none'},
//                          renderer: function(v) { return ''; }
//                          //majorTickSteps: 50
//                          //calculateCategoryCount: true
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
                            }
                        }
                    ]
                }
            );

            this.performancePanel.removeAll(true);
            this.performancePanel.add(barChart);
            this.performancePanel.doLayout();
        }
        else
        {
          Ext.Msg.alert('Part Performance', 'There is an error. The part performance bar chart can not be generated.');
        }
    },

    generatePartPerformances: function(collectionRecord, parts)
    {
        var part;
        var partPerformances = [];
        var partCount;
        var measurement;
        var measurements;
        var measurementsCount;
        var selectedMeasurements = [];
        var maxMeasurement;
        var collectionId;

        partCount = parts.length;
        collectionId = collectionRecord.get('id');

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
                                selectedMeasurements.push(
                                    {
                                        biofabId: part.displayID,
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
    }
});
