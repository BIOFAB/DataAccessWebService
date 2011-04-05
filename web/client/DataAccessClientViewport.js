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
    constructLoadCount: 0,
	
    initComponent: function()
    {
        DataAccessClientViewport.superclass.initComponent.call(this);

        var collectionsGridSelectionModel = this.collectionsGridPanel.getSelectionModel();
	collectionsGridSelectionModel.on('rowselect', this.collectionsGridRowSelectHandler, this);
        this.collectionsGridPanel.getStore().on('load', this.collectionStoreLoadHandler, this);

        var constructsGridSelectionModel = this.constructsGridPanel.getSelectionModel();
	constructsGridSelectionModel.on('rowselect', this.constructsGridRowSelectHandler, this);
		
	var partsGridSelectionModel = this.partsGridPanelRef.getSelectionModel();
	partsGridSelectionModel.on('rowselect', this.partsGridRowSelectHandler, this);

//      var partsGridSelectionModel = this.partsGridPanel.getSelectionModel();
//	partsGridSelectionModel.on('rowselect', this.partsGridRowSelectHandler, this);

        this.promotersButtonRef.setHandler(this.promotersButtonClickHandler, this);
        this.fiveUTRButtonRef.setHandler(this.fiveUTRButtonClickHandler, this);
        this.cdsButtonRef.setHandler(this.cdsButtonClickHandler, this);

	this.constructsGridPanel.getStore().on('load', this.constructStoreForDisplayLoadHandler, this);
        this.constructPartStore = new ConstructPartStore();

        this.collectionsGridExportButtonRef.setHandler(this.collectionsGridExportButtonClickHandler, this);
        this.partsGridExportButton.setHandler(this.partsGridExportButtonClickHandler, this);
        this.constructsGridExportButton.setHandler(this.constructsGridExportButtonClickHandler, this);
        this.showAllPartsButtonRef.setHandler(this.showAllPartsButtonClickHandler, this);
        this.showAllConstructsButtonRef.setHandler(this.showAllConstructsButtonClickHandler, this);

        this.helpButtonRef.setHandler(this.helpButtonClickHandler, this);
    },

    collectionsGridRowSelectHandler: function(selectModel, rowIndex, record)
    {
        var id = record.get('id');
        
        if(id !== 4)
        {
            this.constructsGridPanel.getStore().clearFilter();
            this.repopulateConstructStore();
            var partStore = this.partsGridPanelRef.getStore();
            var constructStore = this.constructsGridPanel.getStore();

            partStore.filter([
            {
                property     : 'collectionID',
                value        : id,
                anyMatch     : true,
                exactMatch   : true
            }]);

            constructStore.filter([
            {
                property     : 'collection_id',
                value        : id,
                anyMatch     : true,
                exactMatch   : true
            }]);

            var collectionName = record.get('name');
            this.partsGridPanelRef.setTitle(collectionName + ' Parts');
            this.constructsGridPanel.setTitle(collectionName + ' Constructs');
            this.showCollectionPanel(record);
        }
        else
        {
             this.showCollectionPanel(record);
        }
    },
    
    partsGridRowSelectHandler: function(selectModel, rowIndex, record)
    {
        var partID = record.get("biofabID");
        var description = record.get('description');
        var relationRecord = null;
        var constructID = null;
        var constructRecord = null;
        var constructRecords = null;
        var constructRecordsForDisplay = [];
        var relationPartID = null;
        var relationsCount = this.constructPartStore.getCount();

        for(var i = 0; i < relationsCount; i += 1)
        {
            relationRecord = this.constructPartStore.getAt(i);
            relationPartID = relationRecord.get("partID");

            if(relationPartID.toUpperCase() === partID.toUpperCase())
            {
                    constructID = relationRecord.get("constructID");
                    constructRecords = this.constructStore.query('biofab_id', new RegExp(constructID), false, false, true);
                    constructRecord = constructRecords.itemAt(0);

                    if(constructRecord !== null && constructRecord !== undefined)
                    {
                        constructRecordsForDisplay.push(constructRecord);
                    }
            }
        }

        if(constructRecordsForDisplay.length > 0)
        {
            this.constructsGridPanel.getStore().removeAll();
            this.constructsGridPanel.getStore().add(constructRecordsForDisplay);
            this.constructsGridPanel.setTitle('Constructs with ' + partID);
        }
        else
        {
            Ext.Msg.alert('Data Access Client', 'No construct has ' + description);
        }

        
    },
	
        constructsGridRowSelectHandler: function(selectModel, rowIndex, record)
	{
	    var biofabID = record.get("biofab_id");
            var id = record.get('id');

//            if(id === 2)
//            {
//                Ext.Msg.alert('Modular Promoter Library', 'At this time, only the promoter sequences are available for the constructs in the Modular Promoter Library.\n'+
//                'The complete sequence with annotations will be available in an upcoming release of the Data Access Client.');
//            }
//
//            if(id === 3)
//            {
//                Ext.Msg.alert('Random Promoter Library', 'At this time, only the promoter sequences are available for the constructs in the Random Promoter Library.\n'+
//                'The complete sequence with annotations will be available in an upcoming release of the Data Access Client.');
//            }
//
//            if(id === 4)
//            {
//                Ext.Msg.alert('Terminator Library', 'At this time, only the terminator sequences are available for the constructs in the Terminator Library.\n'+
//                'The complete sequence with annotations will be available in an upcoming release of the Data Access Client.');
//            }

	    this.showDatasheet(biofabID);
	},

        constructStoreForDisplayLoadHandler: function(store, records, options)
        {
            if(this.constructStore === null)
            {
               this.constructStore = new ConstructStore();
               this.constructStore.on('load', this.constructStoreLoadHandler, this);
            }
            
            this.constructStore.load({callback: this.constructStoreLoadHandler, scope:this, add:false});
        },
	
	constructStoreLoadHandler: function(store, records, options)
	{
            var countA = this.constructStore.getCount();
            var countB = this.constructsGridPanel.getStore().getCount();
            this.constructLoadCount += 1;

            if(countA !== countB && this.constructLoadCount < 10 && countA === 0)
            {
               this.constructStore.load({callback: this.constructStoreLoadHandler, scope:this, add:false});
            }
	},

        collectionsGridExportButtonClickHandler: function(button, event)
        {
            var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'collections?format=csv',"Collections","width=640,height=480");
            exportWindow.scrollbars.visible = true;
            exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        partsGridExportButtonClickHandler: function(button, event)
        {
            var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'annotatedparts?format=json',"Annotated Parts","width=640,height=480");
            exportWindow.scrollbars.visible = true;
            exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        constructsGridExportButtonClickHandler: function(button, event)
        {
            var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'constructs' + "?collectionid=1" + "&format=csv","Constructs","width=640,height=480");
            exportWindow.scrollbars.visible = true;
            exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        showAllPartsButtonClickHandler:function(button, event)
        {
            this.partsGridPanelRef.setTitle('Parts');
            this.partsGridPanelRef.getStore().clearFilter(false);
        },

        showAllConstructsButtonClickHandler: function(button, event)
        {
            this.constructsGridPanel.getStore().clearFilter();
            this.repopulateConstructStore();
        },

        //Refactor!!!
        repopulateConstructStore:function()
        {
            var record = null;
            var constructRecordsForDisplay = [];
            var count = this.constructStore.getCount();

            for(var i = 0; i < count; i += 1)
            {
                record = this.constructStore.getAt(i);
                constructRecordsForDisplay.push(record);
            }

            this.constructsGridPanel.getStore().removeAll();
            this.constructsGridPanel.getStore().add(constructRecordsForDisplay);
            this.constructsGridPanel.setTitle('Constructs');
        },

        collectionStoreLoadHandler: function(store, records, options)
        {
		var collectionRecord = store.getAt(0);
		this.showCollectionPanel(collectionRecord);

		//TODO Deal with case where the constructs could not be loaded
        },

        promotersButtonClickHandler: function(button, event)
        {
            this.partsGridPanelRef.getStore().filter([{property: 'biofabType', value: "promoter", anyMatch: true, caseSensitive: false}]);
        },

        fiveUTRButtonClickHandler: function(button, event)
        {
            this.partsGridPanelRef.getStore().filter([{property: 'biofabType', value: "5' UTR", anyMatch: true, caseSensitive: false}]);
        },

        cdsButtonClickHandler: function(button, event)
        {
            this.partsGridPanelRef.getStore().filter([{property: 'biofabType', value: "CDS", anyMatch: true, caseSensitive: false}]);
        },

        helpButtonClickHandler: function(button, event)
        {
            var helpWindow = window.open(HELP_PAGE);
            helpWindow.scrollbars.visible = true;
        },
	
/*
 * 
 * 	Protected Methods
 * 
 */	
	
	showDatasheet: function(constructID)
	{		
            var datasheetPanel = new DatasheetPanel();
            datasheetPanel.setTitle(constructID);
            var tab = this.infoTabPanel.add(datasheetPanel);
            this.infoTabPanel.doLayout();
            this.infoTabPanel.setActiveTab(tab);
            tab.fetchData(constructID);
            //datasheetPanel.setConstructID(constructID);
	},

        showCollectionPanel: function( collectionRecord )
        {
            var id = collectionRecord.get('id');
            var collectionPanel;
            var tab;

            if(id === 1)
            {
                collectionPanel = new PilotProjectPanel();
                collectionPanel.setCollectionRecord(collectionRecord);
                tab = this.infoTabPanel.add(collectionPanel);
                this.infoTabPanel.doLayout();
                this.infoTabPanel.setActiveTab(tab);

            }

            if(id === 2)
            {
//                collectionPanel = new ModularPromoterPanel();
//                tab = this.infoTabPanel.add(collectionPanel);
//                this.infoTabPanel.setActiveTab(tab);
//                collectionPanel.setCollectionRecord(collectionRecord);
//                this.infoTabPanel.setActiveTab(tab);

                Ext.Msg.alert('Modular Promoter Library', 'A collection performance panel for the Modular Promoter Library is under development and will be added to the Data Access Client in an upcoming release.');
            }

            if(id === 3)
            {
//                collectionPanel = new ModularPromoterPanel();
//                tab = this.infoTabPanel.add(collectionPanel);
//                this.infoTabPanel.setActiveTab(tab);
//                collectionPanel.setCollectionRecord(collectionRecord);
//                this.infoTabPanel.setActiveTab(tab);

                Ext.Msg.alert('Random Promoter Library', 'A collection performance panel for the Random Promoter Library is under development and will be added to the Data Access Client in an upcoming release.');
            }

            if(id === 4)
            {
                collectionPanel = new TerminatorPanel();
                tab = this.infoTabPanel.add(collectionPanel);
                this.infoTabPanel.setActiveTab(tab);
                collectionPanel.setCollectionRecord(collectionRecord);
                this.infoTabPanel.setActiveTab(tab);

//                Ext.Msg.alert('Terminator Library', 'A collection performance panel for the Terminator Library is under development and will be added to the Data Access Client in an upcoming release.');
            }
        }
});
