/*
 *
 * File: DatasheetPanel.js
 * 
 */

DatasheetPanel = Ext.extend(DatasheetPanelUi,{
    constructID: null,
    construct: null,

    initComponent: function()
    {
        DatasheetPanel.superclass.initComponent.call(this);

        this.designPanelExportButtonRef.setHandler(this.designPanelExportButtonClickHandler, this);
        this.performancePanelExportButtonRef.setHandler(this.performancePanelExportButtonClickHandler, this);
        
        //this.performancePanelRef.on('afterrender', this.performancePanelAfterRenderHandler, this);
        //this.bulkGeneExpressionPanelRef.on('afterrender', this.bulkGeneExpressionPanelAfterRenderHandler, this);
        this.geneExpressionPerCellPanelRef.on('afterrender', this.geneExpressionPerCellPanelAfterRenderHandler, this);
    },

    setConstructID: function(constructID)
    {
        this.constructID = constructID;
        this.setTitle(constructID);
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
            panelConfig = {
                xtype:'panel',
                title: 'Bulk Gene Expression for ' + this.constructID,
                layout: 'auto',
                width: 400,
                //height: 400,
                ref: '../../bulkGeneExpressionPanelRef',
                region: 'west',
                split: true
            }

            this.performancePanelRef.add(panelConfig);
            this.bulkGeneExpressionPanelRef.on('afterrender', this.bulkGeneExpressionPanelAfterRenderHandler, this);
            this.performancePanelRef.doLayout();
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

    bulkGeneExpressionPanelAfterRenderHandler:function(component)
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
       
        var element = component.getEl();

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

        component.add(newPanel);
        component.doLayout();
    },

    geneExpressionPerCellPanelAfterRenderHandler: function(component)
    {
        var chartConfig;
        var newPanel;

        this.performancePanelTextRef.setVisible(true);

        function generateData(n)
        {
            var data = [],
            p = (Math.random() *  11) + 1,
            i;
            
            for (i = 0; i < (n || 100); i++) {
                data.push({
                    data1: Math.floor(Math.max((Math.random() * 100), 20)),
                    data2: Math.floor(Math.max((Math.random() * 100), 20))
                });
            }

            return data;
        }

        var store1 = new Ext4.data.JsonStore({
            fields: ['data1', 'data2'],
            data: generateData()
        });

        store1.loadData(generateData());
        var element = component.getEl();
        
        chartConfig = {
            xtype: 'chart',
            theme: 'Category1',
            flex: 1,
            animate: false,
            store: store1,
            axes: [
                {
                    type: 'Numeric',
                    position: 'left',
                    fields: ['data1'],
                    title: 'Fluorescence',
                    labelTitle: {font: '12px Arial'},
                    label: {font: '11px Arial'}
                },
                {
                    type: 'Numeric',
                    position: 'bottom',
                    fields: ['data2'],
                    title: 'Side Scatter',
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
                xField: 'data1',
                yField: 'data2',
                color: '#a00'
            }]
        };

        newPanel = Ext4.ClassManager.instantiate('Ext.panel.Panel', {
            width: 360,
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

        component.add(newPanel);
        component.doLayout();
        this.performancePanelTextRef.setVisible(false);
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
    }
});
