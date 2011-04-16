/*
 *
 * File: DatasheetPanel.js
 * 
 */

DatasheetPanel = Ext.extend(DatasheetPanelUi,{
    constructID: null,
    construct: null,
    displayWarning: true,

    initComponent: function()
    {
        DatasheetPanel.superclass.initComponent.call(this);

        this.designPanelExportButtonRef.setHandler(this.designPanelExportButtonClickHandler, this);
        this.performancePanelExportButtonRef.setHandler(this.performancePanelExportButtonClickHandler, this);
        this.showAllEventsButtonRef.setHandler(this.displayAllEventsButtonClickHandler, this);

        //this.performancePanelRef.setActiveTab(1);
        //this.performancePanelRef.setActiveTab(0);
    },

    setConstructID: function(constructID)
    {
        this.constructID = constructID;
        this.setTitle(constructID);
        this.fetchDesign(constructID);
        this.fetchPerformance(constructID);
    },

    fetchData: function(constructID)
    {
        this.constructID = constructID;
        this.fetchDesign(constructID);
        this.fetchPerformance(constructID);
    },

    fetchDesign:function(constructID)
    {
        this.designPanelText.setVisible(true);
        Ext.Ajax.request({
                   url: WEB_SERVICE_BASE_URL + 'construct/design',
                   method: "GET",
                   success: this.fetchDesignResultHandler,
                   failure: this.fetchDesignErrorHandler,
                   params: {
                                id: constructID,
                                format: 'insd'
                            },
                   scope: this
        });
    },

    fetchPerformance:function(constructID)
    {
        this.performancePanelTextRef.setVisible(true);

        Ext.Ajax.request({
                   url: WEB_SERVICE_BASE_URL + 'construct/performance',
                   method: "GET",
                   success: this.fetchPerformanceResultHandler,
                   failure: this.fetchPerformanceErrorHandler,
                   params: {
                                id: constructID,
                                format: 'json'
                            },
                   scope: this
        });
    },

    fetchDesignResultHandler: function(response, opts)
    {
            this.designPanelText.setVisible(false);
            var flash = new Ext.FlashComponent(
                {
                    url:'designviewer/DesignViewer.swf',
                    flashVars:{design:response.responseText}
                }
            );
                
            this.designPanel.add(flash);
            this.designPanel.doLayout();
    },

    fetchDesignErrorHandler: function(response, opts)
    {
       this.designPanelText.setVisible(false);
       Ext.Msg.alert('Fetch Design', 'There was an error while attempting to fetch the design.\n' + 'Error: ' + response.responseText);
    },

    fetchPerformanceResultHandler: function(response, opts)
    {
        var panelConfig;

        this.performancePanelTextRef.setVisible(false);

        if(response.responseText.length > 0)
        {
            this.construct = Ext.util.JSON.decode(response.responseText);
            this.displayTimeSeriesPlot(this.construct);
            this.displayScatterPlot(this.construct, false);
        }
        else
        {
             //TODO manage the null case
        }
    },

    fetchPerformanceErrorHandler: function(response, opts)
    {
        this.performancePanelTextRef.setVisible(false);
//       Ext.Msg.alert('Construct Performance', 'Construct Performance Data for ' + this.constructID + ' will be available in an upcoming release of the Data Access Client.');
//       this.performancePanelRef.hide();
//       this.performancePanelRef.disable();
//       this.datasheetTabPanel.setActiveTab(0);

        this.performancePanelRef.removeAll();

        this.performancePanelRef.add(
            {
                xtype:'panel',
                title: 'Under Development',
                layout: 'auto',
                html: 'Performance Data for ' + this.constructID + ' will be available in an upcoming release of the Data Access Client.'
            }
        )

        this.performancePanelRef.doLayout();
        this.performancePanelRef.setActiveTab(0);
        this.datasheetTabPanel.setActiveTab(0);
    },

    displayTimeSeriesPlot:function(construct)
    {
        var store;
        var chartConfig;
        var newPanel;
        var measurements = [];
        var measurementsCount = this.construct.performance.reads[1].measurements.length;
        var measurement;
        var fluorescenceID,
            fluorescenceTime,
            fluorescence,
            odID,
            odTime,
            od;

        Ext4.regModel('Measurement', {
            fields: [
                {name: 'fluorescenceID', type: 'int'},
                {name: 'fluorescenceTime', type: 'int'},
                {name: 'fluorescence', type: 'float'},
                {name: 'odID', type: 'int'},
                {name: 'odTime', type: 'int'},
                {name: 'od', type: 'float'}
            ]
        });

        for(var i = 0; i < measurementsCount; i += 1)
        {
            fluorescenceID = this.construct.performance.reads[1].measurements[i].id;
            fluorescenceTime = this.construct.performance.reads[1].measurements[i].time;
            fluorescence = this.construct.performance.reads[1].measurements[i].value;
            odID = this.construct.performance.reads[0].measurements[i].id;
            odTime = this.construct.performance.reads[0].measurements[i].time;
            od = this.construct.performance.reads[0].measurements[i].value;

            measurement = {
                fluorescenceID: fluorescenceID,
                fluorescenceTime: fluorescenceTime,
                fluorescence: fluorescence,
                odID: odID,
                odTime: odTime,
                od: od
            }

            measurements.push(measurement);
        }

        store = new Ext4.data.Store({
            model: 'Measurement',
            data : measurements
        });

        chartConfig = {
            flex: 1,
            xtype: 'chart',
            theme: 'Category1',
            animate: false,
            store: store,
//            legend: {
//                position: 'right',
//                padding: 0,
//                boxZIndex:0
//            },
            axes: [
                {
                    type: 'Numeric',
                    position: 'left',
                    fields: ['fluorescence'],
                    title: 'Fluorescence (AU)',
                    grid: false,
                    labelTitle: {font: '12px Arial'},
                    label: {font: '11px Arial'}
                    //minimum: 10
                },
                {
                    type: 'Numeric',
                    position: 'right',
                    fields: ['od'],
                    title: 'Optical Density (Absorbance at 600 nm)',
                    grid: false,
                    labelTitle: {font: '12px Arial'},
                    label: {font: '11px Arial'}
                },
                {
                    type: 'Numeric',
                    position: 'bottom',
                    fields: ['fluorescenceTime','odTime'],
                    title: 'Time (minutes)',
                    //dateFormat: 'G:i',
                    grid: false,
                    labelTitle: {font: '12px Arial'},
                    label: {font: '11px Arial'}
                },

            ],
            series: [{
                title: 'Replicate 1 - Fluorescence',
                type: 'line',
                lineWidth: 2,
                showMarkers: true,
                fill: true,
                axis: 'left',
                showInLegend: true,
                xField: 'fluorescenceTime',
                yField: 'fluorescence',
//                style: {
//                    'stroke-width': 1
//                },
                markerCfg: {
                    type: 'circle',
                    size: 2,
                    radius: 2,
                    'stroke-width': 0
                },
                tips: {
                    trackMouse: true,
                    width: 80,
                    height: 40,
                    renderer: function(storeItem, item) {
                        this.setTitle(storeItem.get('fluorescence'));
                    }
                }
            },
//            {
//                title: 'Replicate 1 - OD',
//                type: 'line',
//                lineWidth: 2,
//                showMarkers: true,
//                fill: true,
//                axis: 'right',
//                xField: 'odTime',
//                yField: 'od',
////                style: {
////                    'stroke-width': 1
////                },
//                markerCfg: {
//                    type: 'circle',
//                    size: 2,
//                    radius: 2,
//                    'stroke-width': 0
//                },
//                tips: {
//                    trackMouse: true,
//                    width: 80,
//                    height: 40,
//                    renderer: function(storeItem, item) {
//                        this.setTitle(storeItem.get('od'));
//                    }
//                }
//            }
        ]
        };

        var element = this.bulkGeneExpressionPanelRef.getEl();

        newPanel = Ext4.ClassManager.instantiate('Ext.panel.Panel', {
            width: 500,
            height: 300,
            renderTo: element.dom,
            layout: 'fit',
            items: [{
                    flex: 1,
                    xtype: 'container',
                    layout: 'fit',
                    items:[chartConfig]
                }
            ]
        });

        this.bulkGeneExpressionPanelRef.add(newPanel);
        this.bulkGeneExpressionPanelRef.doLayout();
    },

    displayScatterPlot: function(construct, displayAll)
    {
        var newStore;
        var newChartConfig;
        var anotherPanel;
        var cytoMeasurements;

        this.performancePanelRef.setActiveTab(1);
        this.geneExpressionPerCellPanelRef.removeAll(true);

        Ext4.regModel('CytometerMeasurement', {
            fields: [
                {name: 'id', type: 'int'},
                {name: 'fluorescence', type: 'float'},
                {name: 'forwardScatter', type: 'float'},
                {name: 'sideScatter', type: 'float'}
            ]
        });

        if(this.construct !== null)
        {
            cytoMeasurements = construct.performance.cytometerReads[0].measurements;

            var measurement = null;
            var dataSet = [];
            var dataCount = cytoMeasurements.length;
            var randomIndex;
            var shouldPush;
            var sampleSize = Math.round(dataCount*.25);

            if(sampleSize > 1000)
            {
                sampleSize = 1000;
            }

            if(displayAll)
            {
                dataSet = cytoMeasurements;
            }
            else
            {
                for(var i = 0; i < sampleSize; i += 1)
                {
                    shouldPush = true;
                    randomIndex = Math.round((Math.random() * 1000000))%dataCount;
                    measurement = cytoMeasurements[randomIndex];

                    for(var j = 0; j < dataSet.length; j += 1)
                    {
                        if(dataSet[j] === randomIndex)
                        {
                           shouldPush = false;
                        }
                    }

                    if(shouldPush)
                    {
                        dataSet.push(measurement);
                    }
                }
            }

            this.dataDisplayedTextRef.setText(dataSet.length + ' of ' + dataCount + ' Events Displayed');

            newStore = new Ext4.data.Store({
                model: 'CytometerMeasurement',
                data : dataSet
            });

            var element = this.geneExpressionPerCellPanelRef.getEl();

            newChartConfig = {
                xtype: 'chart',
                theme: 'Category1',
                flex: 1,
                animate: false,
                store: newStore,
//                legend: {
//                    position: 'right'
//                },
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        fields: ['forwardScatter'],
                        title: 'Forward Scatter',
                        labelTitle: {font: '12px Arial'},
                        label: {font: '11px Arial'}
                    },
                    {
                        type: 'Numeric',
                        position: 'bottom',
                        fields: ['fluorescence'],
                        title: 'Fluorescence',
                        grid: false,
                        labelTitle: {font: '12px Arial'},
                        label: {font: '11px Arial'}
                    }
                ],
                series: [{
                    title: 'Replicate 1',
                    type: 'scatter',
                    markerCfg: {
                        radius: 1,
                        size: 1
                    },
                    axis: 'left',
                    xField: 'fluorescence',
                    yField: 'forwardScatter',
                    color: '#a00'
                }]
            };

            anotherPanel = Ext4.ClassManager.instantiate('Ext.panel.Panel', {
                width: 360,
                height: 300,
                renderTo: element.dom,
                layout: 'fit',
                items: [{
                        flex: 1,
                        xtype: 'container',
                        layout: 'fit',
                        items:[newChartConfig]
                    }
                ]
            });

            this.geneExpressionPerCellPanelRef.add(anotherPanel);
            this.geneExpressionPerCellPanelRef.doLayout();
        }
        else
        {
          Ext.Msg.alert('Performance Data', 'Data not available yet.');
        }

        this.showAllEventsButtonRef.enable();
        //this.performancePanelRef.setActiveTab(0);
    },

    designPanelExportButtonClickHandler: function(button, event)
    {
        var genbankWindow = window.open(WEB_SERVICE_BASE_URL + 'construct/design' + "?id=" + this.constructID + "&format=genbank","Genbank File for " + this.constructID,"width=640,height=480");
        genbankWindow.alert("Use File/Save As in the menu bar to save this document.");
        genbankWindow.scrollbars.visible = true;
    },

    performancePanelExportButtonClickHandler: function(button, event)
    {
        var expWindow = window.open(WEB_SERVICE_BASE_URL + 'construct/performance' + "?id=" + this.constructID + "&format=json","JSON File for " + this.constructID,"width=640,height=480");
        expWindow.scrollbars.visible = true;
        expWindow.alert("Use File/Save As in the menu bar to save this document.");
    },

    displayAllEventsButtonClickHandler: function(button, event)
    {
//        if(this.displayWarning)
//        {
//            Ext.Msg.alert('Gene Expresssion per Cell', 'Displaying all the data can take upto 10 seconds.');
//            this.displayWarning = false;
//        }

        this.displayScatterPlot(this.construct, true);
    }
});
