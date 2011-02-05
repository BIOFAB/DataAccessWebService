/*
 * 
 * File: DataAccessClientViewport.js
 * 
 * 
 */

DataAccessClientViewport = Ext.extend(DataAccessClientViewportUi, 
{
    constructStore: null,
    constructHasPartStore: null,
    performanceStore: null,
	
    initComponent: function()
    {
        DataAccessClientViewport.superclass.initComponent.call(this);

        var collectionsGridSelectionModel = this.constructsGridPanel.getSelectionModel();
	collectionsGridSelectionModel.on('rowselect', this.collectionsGridRowSelectHandler, this);

        var constructsGridSelectionModel = this.constructsGridPanel.getSelectionModel();
	constructsGridSelectionModel.on('rowselect', this.constructsGridRowSelectHandler, this);
		
	var partsGridSelectionModel = this.partsGridPanel.getSelectionModel();
	partsGridSelectionModel.on('rowselect', this.partsGridRowSelectHandler, this);
		
	this.constructsGridPanel.getStore().on('load', this.constructStoreLoadHandler, this);
		
	this.constructHasPartStore = new ConstructHasPartStore();
	this.constructStore = new ConstructStore();
	//this.performanceStore = new PerformanceStore();

        this.partsGridExportButton.setHandler(this.partsGridExportButtonClickHandler, this);
        this.constructsGridExportButton.setHandler(this.constructsGridExportButtonClickHandler, this);
    },

    collectionsGridRowSelectHandler: function(selectModel, rowIndex, record)
    {

    },
    
    partsGridRowSelectHandler: function(selectModel, rowIndex, record)
	{
	    var partID = record.get("id");
	    var relationRecord = null;
	    var constructID = null;
	    var constructRecord = null;
	    var constructRecords = null;
	    var constructRecordsForDisplay = [];
	    var relationPartID = null;
	    var relationsCount = this.constructHasPartStore.getCount();
	    
	    for(var i = 0; i < relationsCount; i += 1)
	    {
	    	relationRecord = this.constructHasPartStore.getAt(i);
	    	relationPartID = relationRecord.get("part");
	    	
	    	if(relationPartID.toUpperCase() === partID.toUpperCase())
	    	{
	    		constructID = relationRecord.get("construct");
		    	constructRecords = this.constructStore.query('id', new RegExp(constructID), false, false, true);
		    	constructRecord = constructRecords.itemAt(0);
		    	constructRecordsForDisplay.push(constructRecord);
	    	}
	    }
	    
	    this.constructsGridPanel.getStore().removeAll();
	    this.constructsGridPanel.getStore().add(constructRecordsForDisplay);
    	this.constructsGridPanel.setTitle('Constructs with ' + partID);
	},
	
        constructsGridRowSelectHandler: function(selectModel, rowIndex, record)
	{
	    var id = record.get("id");
	    this.showDatasheet(id);
	},
	
	constructStoreLoadHandler: function(store, records, options)
	{
		var record = store.getAt(0);
		var id = record.get("id");
		this.showDatasheet(id);
		
		//TODO Deal with case where the constructs could not be loaded
	},

        partsGridExportButtonClickHandler: function(button, event)
        {
            var csvWindow = window.open(WEB_SERVICE_BASE_URL + 'annotatedparts' + "?projectid=1" + "&format=csv","Annotated Parts","width=640,height=480");
            csvWindow.scrollbars.visible = true;
            csvWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        constructsGridExportButtonClickHandler: function(button, event)
        {
            var csvWindow = window.open(WEB_SERVICE_BASE_URL + 'constructs' + "?projectid=1" + "&format=csv","Constructs","width=640,height=480");
            csvWindow.scrollbars.visible = true;
            csvWindow.alert("Use File/Save As in the menu bar to save this document.");
        },
	
/*
 * 
 * 	Protected Methods
 * 
 */	
	
	showDatasheet: function(componentID)
	{		
		var datasheetPanel = new DatasheetPanel();
                datasheetPanel.setComponentID(componentID);
		var tab = this.infoTabPanel.add(datasheetPanel);
		this.infoTabPanel.setActiveTab(tab);
                //tab.renderData();
	}
});
