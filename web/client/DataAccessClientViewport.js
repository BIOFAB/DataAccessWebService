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
    parts: null,
	
    initComponent: function()
    {
        DataAccessClientViewport.superclass.initComponent.call(this);

        var collectionsGridSelectionModel = this.collectionsGridPanel.getSelectionModel();
	collectionsGridSelectionModel.on('rowselect', this.collectionsGridRowSelectHandler, this);
        this.collectionsGridPanel.getStore().on('load', this.collectionStoreLoadHandler, this);

        var constructsGridSelectionModel = this.constructsGridPanel.getSelectionModel();
	constructsGridSelectionModel.on('rowselect', this.constructsGridRowSelectHandler, this);
		
	var partsGridSelectionModel = this.partsGridPanel.getSelectionModel();
	partsGridSelectionModel.on('rowselect', this.partsGridRowSelectHandler, this);
        this.fetchParts();
        //this.partsGridPanel.getStore().on('load', this.partStoreLoadHandler, this);

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
    },

    collectionsGridRowSelectHandler: function(selectModel, rowIndex, record)
    {
        var id = record.get('id');
        
        if(id !== 4)
        {
            this.constructsGridPanel.getStore().clearFilter();
            this.repopulateConstructStore();
            var partStore = this.partsGridPanel.getStore();
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
            this.partsLabel.setText(collectionName + ' Parts');
            this.constructsLabel.setText(collectionName + ' Constructs');
            this.showCollectionPanel(record);
        }
        else
        {
             this.showCollectionPanel(record);
        }
    },
    
    partsGridRowSelectHandler: function(selectModel, rowIndex, record)
    {
        var partID = record.get("displayID");
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
            this.constructsLabel.setText('Constructs with ' + partID);
        }
        else
        {
            Ext.Msg.alert('Data Access Client', 'No construct has ' + description);
        }

        this.showPartPanel(record);

        
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
            var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'collections?format=json',"Collections","width=640,height=480");
            exportWindow.scrollbars.visible = true;
            exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        partsGridExportButtonClickHandler: function(button, event)
        {
            var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'parts?format=json',"Parts","width=640,height=480");
            exportWindow.scrollbars.visible = true;
            exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        constructsGridExportButtonClickHandler: function(button, event)
        {
            var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'constructs?format=csv',"Constructs","width=640,height=480");
            exportWindow.scrollbars.visible = true;
            exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        },

        showAllPartsButtonClickHandler:function(button, event)
        {
            this.partsLabel.setText('Parts', false);
            this.partsGridPanel.getStore().clearFilter(false);
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
            this.constructsLabel.setText('Constructs');
        },

        collectionStoreLoadHandler: function(store, records, options)
        {
		var collectionRecord = store.getAt(0);
		this.showCollectionPanel(collectionRecord);

		//TODO Deal with case where the constructs could not be loaded
        },

        promotersButtonClickHandler: function(button, event)
        {
            this.partsGridPanel.getStore().filter([{property: 'type', value: "promoter", anyMatch: true, caseSensitive: false}]);
            this.partsLabel.setText('Parts: Promoters');
        },

        fiveUTRButtonClickHandler: function(button, event)
        {
            this.partsGridPanel.getStore().filter([{property: 'type', value: "5' UTR", anyMatch: true, caseSensitive: false}]);
            this.partsLabel.setText('Parts: 5\' UTR');
        },

        cdsButtonClickHandler: function(button, event)
        {
            this.partsGridPanel.getStore().filter([{property: 'type', value: "CDS", anyMatch: true, caseSensitive: false}]);
            this.partsLabel.setText('Parts: CDS');
        },

        fetchPartsResultHandler: function(response, opts)
        {
            var partsForStore = [];
            var part;
            var partForStore;
            var partsCount;
            var measurement;
 
            if(response.responseText.length > 0)
            {
                this.parts = Ext.util.JSON.decode(response.responseText);
                partsCount = this.parts.length;

                for(var i = 0; i < partsCount; i += 1)
                {
                    part = this.parts[i];
                    measurement = this.retrieveMeasurement(part);
                    partForStore = {
                        collectionID: part.collectionID,
                        displayID: part.displayID,
                        type: part.type,
                        description: part.description,
                        dnaSequence: part.dnaSequence.nucleotides,
                        measurementLabel: measurement.label,
                        measurementValue: measurement.value,
                        measurementUnit: measurement.unit
                    }
                    partsForStore.push(partForStore);
                }

                this.partsGridPanel.getStore().loadData(partsForStore, false);
                this.partsGridPanel.getStore().filter([{property: 'type', value: "promoter", anyMatch: true, caseSensitive: false}]);
                this.partsLabel.setText('Parts: Promoters');
            }
            else
            {
                  Ext.Msg.alert('Fetch Parts', 'There was an error while attempting to fetch the parts. Please reload the Data Access Client.\n' + 'Error: ' + response.responseText);
            }
        },

        fetchPartsErrorHandler: function(response, opts)
        {
            Ext.Msg.alert('Fetch Parts', 'There was an error while attempting to fetch the parts. Please reload the Data Access Client.\n' + 'Error: ' + response.responseText);
        },

//        partStoreLoadHandler: function(store, records, options)
//        {
//            this.partsGridPanel.getStore().filter([{property: 'type', value: "promoter", anyMatch: true, caseSensitive: false}]);
//            this.partsLabel.setText('Parts: Promoters');
//        },
        
//        helpButtonClickHandler: function(button, event)
//        {
//            var helpWindow = window.open(HELP_PAGE);
//            helpWindow.scrollbars.visible = true;
//        },

	
/*
 * 
 * 	Protected Methods
 * 
 */
        fetchParts:function()
        {
            Ext.Ajax.request({
                       url: WEB_SERVICE_BASE_URL + 'parts?format=json',
                       method: "GET",
                       success: this.fetchPartsResultHandler,
                       failure: this.fetchPartsErrorHandler,
//                       params: {
//                                    id: constructID,
//                                    format: 'json'
//                                },
                       scope: this
            });
        },
	
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

        showCollectionPanel: function(collectionRecord)
        {
            var id = collectionRecord.get('id');
            var collectionPanel;
            var tab;

            if(id === 1)
            {
                collectionPanel = new PilotProjectPanel();

            }

            if(id === 2)
            {
                collectionPanel = new ModularPromoterPanel();
            }

            if(id === 3)
            {
                collectionPanel = new RandomPromoterPanel();
            }

            if(id === 4)
            {
                collectionPanel = new TerminatorPanel();
            }

            if(collectionPanel !== null)
            {
                collectionPanel.setCollectionRecord(collectionRecord);
                tab = this.infoTabPanel.add(collectionPanel);
                this.infoTabPanel.doLayout();
                this.infoTabPanel.setActiveTab(tab);
            }
        },

        showPartPanel: function(partRecord)
        {
            var partPanel;
            var tab;

            partPanel = new PartPanel();

            if(partPanel !== null)
            {
                partPanel.setPartRecord(partRecord);
                tab = this.infoTabPanel.add(partPanel);
                this.infoTabPanel.doLayout();
                this.infoTabPanel.setActiveTab(tab);
            }
        },
        
        retrieveMeasurement: function(part)
        {
            var measurement;
            var performance = part.performance;
            var measurements;
            var measurementsCount;
            var bgeMeasurements = [];
            var maxMeasurement;

            if(performance != undefined)
            {
                measurements = performance.measurements;
                measurementsCount = measurements.length;
                
                for(var i = 0; i < measurementsCount; i += 1)
                {
                    if(measurements[i].type === 'GEC')
                    {
                        //measurement = measurements[i];
                        bgeMeasurements.push(measurements[i]);
                    }
                }

                if(bgeMeasurements.length > 1)
                {
                    bgeMeasurements.sort(
                        function(a,b)
                        {
                            return a.value - b.value;
                        }
                    );

                    maxMeasurement = bgeMeasurements.pop();
                    measurement = {label: 'Maximum ' + maxMeasurement.label, unit: maxMeasurement.unit, value: maxMeasurement.value};
                }
                else
                {
                    if(bgeMeasurements.length === 1)
                    {
                        measurement = bgeMeasurements[0];
                    }
                    else
                    {
                        measurement = {label: 'Unavailable', unit: 'None', value: 0};
                    }
                }
            }
            else
            {
                measurement = {label: 'Unavailable', unit: 'None', value: 0};
            }
            
            return measurement;
        }
});
