/*
 *
 * File: DatasheetPanel.js
 * 
 */

DatasheetPanel = Ext.extend(DatasheetPanelUi,{
    componentID: null,
    timeplot: null,
    resizeTimerID: null,

    initComponent: function()
    {
        DatasheetPanel.superclass.initComponent.call(this);

        this.designPanelExportButtonRef.setHandler(this.designPanelExportButtonClickHandler, this);
        this.performPanelExportButtonRef.setHandler(this.performPanelExportButtonClickHandler, this);
        
        this.performPanelRef.on('afterrender', this.performPanelAfterRenderHandler, this);
        this.bulkGeneExpressionPanelRef.on('afterrender', this.bulkGeneExpressionPanelAfterRenderHandler, this);
        this.geneExpressionPerCellPanelRef.on('afterrender', this.geneExpressionPerCellPanelAfterRenderHandler, this);
    },

    setComponentID: function(componentID)
    {
        this.componentID = componentID;
        this.setTitle(componentID);
        this.fetchDesign(componentID);
    },

    fetchDesign:function(componentID)
    {
        this.designPanelText.setVisible(true);
        Ext.Ajax.request({
                   url: WEB_SERVICE_BASE_URL + 'construct/design',
                   method: "GET",
                   success: this.fetchDesignResultHandler,
                   failure: this.fetchDesignErrorHandler,
                   params: {
                                id: componentID,
                                format: 'insd'
                            },
                   scope: this
        });
    },

    performPanelAfterRenderHandler:function(component)
    {
        this.performPanelTextRef.setVisible(true);
    },

    bulkGeneExpressionPanelAfterRenderHandler:function(component)
    {
        var memoryArray,
        processArray,
        memoryStore,
        processesMemoryStore,
        cpuLoadStore,
        data,
        cpuLoadChartConfig,
        newPanel,
        cpuLoadTimer, pass;

        memoryArray = ['Wired', 'Active', 'Inactive', 'Free'];
        processArray = ['explorer', 'monitor', 'charts', 'desktop', 'Ext3', 'Ext4'];
        colors = ['rgb(244, 16, 0)',
                  'rgb(248, 130, 1)',
                  'rgb(0, 7, 255)',
                  'rgb(84, 254, 0)'];

        Ext4.chart.theme.Memory = Ext4.extend(Ext4.chart.theme.Base, {
            constructor: function(config) {
                Ext4.chart.theme.Base.prototype.constructor.call(this, Ext4.apply({
                    colors: colors
                }, config));
            }
        });

        function generateData(a) {
            var data = [],
                i,
                names = a,
                rest = a.length, total = rest, consume;
            for (i = 0; i < a.length; i++) {
                consume = Math.floor(Math.random() * rest * 100) / 100 + 2;
                rest = rest - (consume - 5);
                data.push({
                    name: names[i],
                    memory: consume
                });
            }

            return data;
        }

        memoryStore = Ext4.create('store.json', {
            fields: ['name', 'memory'],
            data: generateData(memoryArray)
        });

        processesMemoryStore = Ext4.create('store.json', {
            fields: ['name', 'memory'],
            data: generateData(processArray)
        });

        cpuLoadStore = Ext4.create('store.json', { fields: ['core1', 'core2'] });

        data = [];

        function generateCpuLoad() {
            function generate(factor) {
                var value = factor + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 9);

                if (value < 0 || value > 100) {
                    value = 50;
                }

                return value;
            }

            if (data.length === 0) {
                data.push({
                    core1: 0,
                    core2: 0,
                    time: 0
                });

                for (i = 1; i < 100; i++) {
                    data.push({
                        core1: generate(data[i - 1].core1),
                        core2: generate(data[i - 1].core2),
                        time: i
                    });
                }

                cpuLoadStore.loadData(data);
            }
            else {
                cpuLoadStore.data.removeAt(0);
                cpuLoadStore.data.each(function(item, key) {
                    item.data.time = key;
                });

                var lastData = cpuLoadStore.last().data;
                cpuLoadStore.loadData([{
                    core1: generate(lastData.core1),
                    core2: generate(lastData.core2),
                    time: lastData.time + 1
                }], true);
            }

        }

        generateCpuLoad();

        cpuLoadChartConfig = {
            flex: 1,
            xtype: 'chart',
            theme: 'Category1',
            animate: false,
            store: cpuLoadStore,
            legend: {
                position: 'bottom'
            },
            axes: [{
                type: 'Numeric',
                position: 'left',
                minimum: 0,
                maximum: 100,
                fields: ['core1'],
                title: 'Fluorescence',
                grid: true,
                labelTitle: {
                    font: '13px Arial'
                },
                label: {
                    font: '11px Arial'
                }
            }],
            series: [{
                title: 'Read 1',
                type: 'line',
                lineWidth: 4,
                showMarkers: false,
                fill: true,
                axis: 'right',
                xField: 'time',
                yField: 'core1',
                style: {
                    'stroke-width': 1
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
                    items:[
                            cpuLoadChartConfig
                        ]
                }
            ]
        });

        pass = 0;
        function doGenerateCpuLoad() {
            clearTimeout(cpuLoadTimer);
            cpuLoadTimer = setTimeout(function() {
                if (pass % 3 === 0) {
                    memoryStore.loadData(generateData(memoryArray));
                }

                if (pass % 5 === 0) {
                    processesMemoryStore.loadData(generateData(processArray));
                }

                generateCpuLoad();
                doGenerateCpuLoad();
                pass++;
            }, 500);
        }

        //doGenerateCpuLoad();
        
        component.add(newPanel);
        component.doLayout();
    },

    geneExpressionPerCellPanelAfterRenderHandler: function(component)
    {
        var chartConfig;
        var newPanel;

        this.performPanelTextRef.setVisible(true);

        function generateData(n)
        {
            var data = [],
            p = (Math.random() *  11) + 1,
            i;
            
            for (i = 0; i < (n || 1000); i++) {
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
            axes: [{
                type: 'Numeric',
                position: 'left',
                fields: ['data1'],
                title: 'Fluorescence'
            }],
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
        this.performPanelTextRef.setVisible(false);
    },

    fetchDesignResultHandler: function(response, opts)
    {
            this.designPanelText.setVisible(false);
            //Ext.Msg.alert('Fetch Design', response.responseText);
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

    designPanelExportButtonClickHandler: function(button, event)
    {
        var genbankWindow = window.open(WEB_SERVICE_BASE_URL + 'construct/design' + "?id=" + this.componentID + "&format=genbank","Genbank File for " + this.componentID,"width=640,height=480");
        genbankWindow.alert("Use File/Save As in the menu bar to save this document.");
        genbankWindow.scrollbars.visible = true;
    },

    performPanelExportButtonClickHandler: function(button, event)
    {
        var expWindow = window.open(WEB_SERVICE_BASE_URL + 'construct/performance' + "?id=" + this.componentID + "&format=json","JSON File for " + this.componentID,"width=640,height=480");
        expWindow.scrollbars.visible = true;
        expWindow.alert("Use File/Save As in the menu bar to save this document.");
    }
});
