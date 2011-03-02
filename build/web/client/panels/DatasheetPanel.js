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

        this.designPanelExportButton.setHandler(this.designPanelExportButtonClickHandler, this);
        this.performPanelExportButton.setHandler(this.performPanelExportButtonClickHandler, this);
        this.temporalPlotContainer.on('afterrender', this.temporalPlotContainerAfterRenderHandler, this);
        this.histogramContainer.on('afterrender', this.histogramContainerAfterRenderHandler, this);
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

    temporalPlotContainerAfterRenderHandler:function(component)
    {
        var eventSource = new Timeplot.DefaultEventSource();
        var red = new Timeplot.Color('#B9121B');

        var plotInfo = [Timeplot.createPlotInfo({
                id: "plot1",
                dataSource: new Timeplot.ColumnSource(eventSource,1),
                valueGeometry: new Timeplot.DefaultValueGeometry({
                    gridColor: "#000000",
                    axisLabelsPlacement: "left",
                    min: 0
                }),
                timeGeometry: new Timeplot.DefaultTimeGeometry({
                    gridColor: "#000000",
                    axisLabelsPlacement: "top"
                }),
                lineColor: "#ff0000",
                fillColor: "#cc8080",
                showValues: true,
                dotColor: red
            })];

        var element = component.getEl();

        this.timeplot = Timeplot.create(element.dom, plotInfo);
        this.timeplot.loadText("data/timeseries.csv", ",", eventSource);
    },

    histogramContainerAfterRenderHandler: function(component)
    {
      var elementID = component.getId();

      var store = new Ext.data.JsonStore({
          xtype: 'jsonstore',
          root: 'histogram',
          autoload: true,
          url: 'data/histogram.json',
          fields:[
              {name: 'bin', mapping: 'bin'},
              {name: 'freq', mapping: 'freq'}
          ]
      });

        store.load();

      var series = [
          {
              yField:'freq',
              displayName: 'Number of Cells',
              style:{
                  fillColor: 0xFFAAAA,
                  borderColor: 0xAA3333,
                  lineColor: 0xAA3333,
                  size: 35
              }
          }
      ]

      var extraStyle ={
          yAxis: {titleRotation: -90}
      }

      var chart = {
          xtype: 'columnchart',
          store: store,
          xField: 'bin',
          extraStyle: extraStyle,
          series: series,
          xAxis: new Ext.chart.CategoryAxis({title: 'Fluorescence'}),
          yAxis: new Ext.chart.NumericAxis({title: 'Number of Cells'})
      };

      component.add(chart);
      component.doLayout();
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
        var expWindow = window.open(WEB_SERVICE_BASE_URL + 'performance' + "?constructid=" + this.componentID + "&format=csv","CSV File for " + this.componentID,"width=640,height=480");
        expWindow.scrollbars.visible = true;
        expWindow.alert("Use File/Save As in the menu bar to save this document.");
    }
});
