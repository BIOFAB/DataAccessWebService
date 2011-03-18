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
        this.displayAllEventsButtonRef.setHandler(this.displayAllEventsButtonClickHandler, this);
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
       Ext.Msg.alert('Performance Fetch', 'There was an error while attempting to fetch the performance data.\n' + 'Error: ' + response.responseText);
    },

    displayTimeSeriesPlot:function(construct)
    {
        var store;
        var chartConfig;
        var newPanel;
        var measurements;

        Ext4.regModel('Measurement', {
            fields: [
                {name: 'id', type: 'int'},
                {name: 'time', type: 'int'},
                {name: 'value', type: 'float'}
            ]
        });

        measurements = this.construct.performance.reads[1].measurements;

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
//                position: 'bottom'
//            },
            axes: [
                {
                    type: 'Numeric',
                    position: 'left',
                    fields: ['value'],
                    title: 'Fluorescence (AU)',
                    grid: false,
                    labelTitle: {font: '13px Arial'},
                    label: {font: '11px Arial'},
                    minimum: 10
                },
//                {
//                    type: 'Numeric',
//                    position: 'right',
//                    fields: ['value'],
//                    title: 'Optical Density',
//                    grid: false,
//                    labelTitle: {font: '12px Arial'},
//                    label: {font: '11px Arial'}
//                },
                {
                    type: 'Numeric',
                    position: 'bottom',
                    fields: ['time'],
                    title: 'Time (minutes)',
                    //dateFormat: 'G:i',
                    grid: false,
                    labelTitle: {font: '12px Arial'},
                    label: {font: '11px Arial'}
                },

            ],
            series: [{
                title: 'Read 1',
                type: 'line',
                lineWidth: 2,
                showMarkers: true,
                fill: true,
                axis: 'right',
                xField: 'time',
                yField: 'value',
//                style: {
//                    'stroke-width': 1
//                },
                markerCfg: {
                    type: 'circle',
                    size: 3,
                    radius: 3,
                    'stroke-width': 0
                },
                tips: {
                    trackMouse: true,
                    width: 80,
                    height: 40,
                    renderer: function(storeItem, item) {
                        this.setTitle(storeItem.get('value'));
                    }
                }
            }]
        };

        var element = this.bulkGeneExpressionPanelRef.getEl();

        newPanel = Ext4.ClassManager.instantiate('Ext.panel.Panel', {
            width: 400,
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

            if(displayAll)
            {
                dataSet = cytoMeasurements;
            }
            else
            {
                for(var i = 0; i < 500; i += 1)
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

        this.displayAllEventsButtonRef.enable();
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
