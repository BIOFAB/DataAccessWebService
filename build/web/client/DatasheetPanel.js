/*
 * File: DatasheetPanel.js
 */

DatasheetPanel = Ext.extend(DatasheetPanelUi,{
    componentID: null,

    initComponent: function()
    {
        DatasheetPanel.superclass.initComponent.call(this);

        this.designPanelGenbankButton.setHandler(this.designPanelGenbankButtonClickHandler, this);
        this.performPanelCSVButton.setHandler(this.performPanelCSVButtonClickHandler, this);
    },

    setComponentID: function(componentID)
    {
            this.componentID = componentID;
            this.setTitle(componentID);
            this.fetchDesign(componentID);
            this.fetchPerformance(componentID);
    },

    fetchDesign:function(componentID)
    {
        this.designPanelText.setVisible(true);
            Ext.Ajax.request({
                       url: DESIGN_SERVICE_PATH,
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

    fetchPerformance:function(componentID)
    {
        var panel = new Ext.canvasXpress({
            renderTo: this.performancePanel.body,
            showExampleData: false,
            imgDir: 'lib/icons/',
            border: false,
            floating: false,
            shadow: false,
            data: { 
                    y: {
                        vars: ['GFP'],
                        desc: ['Plate Reader Value'],
                        smps: ['0:08:42','0:18:42','0:28:42','0:38:42','0:48:42','0:58:42','1:08:42','1:18:42','1:28:42','1:38:42','1:48:42','1:58:42','2:08:42','2:18:42','2:28:42','2:38:42','2:48:42','2:58:42','3:08:42','3:18:42','3:28:42','3:38:42','3:48:42','3:58:42','4:08:42','4:18:42','4:28:42','4:38:42','4:48:42','4:58:42','5:08:42'],
                        data: [[3,1,6,8,11,1,9,9,14,12,17,18,20,30,37,45,63,79,85,102,121,149,183,212,237,247,257,258,251,259,256]]
                      }
                   },
           options:{graphType: 'Line',
                    animationType: 'grow',
                    showAnimation: true,
                    isGraphTime: true,
                    timeFormat: 'isoTime',
                    xAxisTitle: 'Time',
                    xAxisShow: true,
                    graphOrientation: 'vertical',
                    foreground: 'rgb(0,0,0)',
                    background: 'rgb(255,255,255)',
                    varLabelColor: 'rgb(0,0,0)',
                    axisTickColor: 'rgb(0,0,0)',
                    autoExtend: true,
                    showSampleNames: true,
                    showVariableNames: true,
                    debug: true,
                    margin: 5,
                    legendPosition: 'right',
                    showErrorBars: false,
                    timeTicks: 31,
                    xAxisValues: ['0:08:42','0:18:42','0:28:42','0:38:42','0:48:42','0:58:42','1:08:42','1:18:42','1:28:42','1:38:42','1:48:42','1:58:42','2:08:42','2:18:42','2:28:42','2:38:42','2:48:42','2:58:42','3:08:42','3:18:42','3:28:42','3:38:42','3:48:42','3:58:42','4:08:42','4:18:42','4:28:42','4:38:42','4:48:42','4:58:42','5:08:42']

                   }
        });
        
        this.performancePanel.add(panel);
        this.performancePanel.doLayout();
    },

    fetchDesignResultHandler: function(response, opts)
    {
            this.designPanelText.setVisible(false);
            //Ext.Msg.alert('Fetch Design', response.responseText);
            var flash = new Ext.FlashComponent(
                                                {
                                                    url: DESIGN_VIEWER_URL,
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

    designPanelGenbankButtonClickHandler: function(button, event)
    {
        var genbankWindow = window.open(BASE_URL + 'design' + "?id=" + this.componentID + "&format=genbank","Genbank File for " + this.componentID,"width=640,height=480");
        genbankWindow.alert("Use File/Save As in the menu bar to save this document.");
        genbankWindow.scrollbars.visible = true;
    },

    performPanelCSVButtonClickHandler: function(button, event)
    {
        var csvWindow = window.open(BASE_URL + 'performance' + "?constructid=" + this.componentID + "&format=csv","CSV File for " + this.componentID,"width=640,height=480");
        csvWindow.scrollbars.visible = true;
        csvWindow.alert("Use File/Save As in the menu bar to save this document.");
    }
});
