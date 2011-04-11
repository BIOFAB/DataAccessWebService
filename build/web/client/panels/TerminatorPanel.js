/*
 *
 * File: PilotProjectPanel.js
 * 
 */

TerminatorPanel = Ext.extend(TerminatorPanelUi,{
    collectionRecord: null,

    initComponent: function()
    {
        TerminatorPanel.superclass.initComponent.call(this);

        this.terminatorsGridExportButtonRef.setHandler(this.terminatorsGridExportButtonClickHandler, this);
    },

    //
    //  Public Methods
    //
    
    setCollectionRecord: function(collectionRecord)
    {
            var description = null;

            this.collectionRecord = collectionRecord;
            description = collectionRecord.get('description');
            this.collectionTextAreaRef.setValue(description);
    },

    terminatorsGridExportButtonClickHandler: function(button, event)
    {
        var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'terminators?format=json');
        exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        exportWindow.scrollbars.visible = true;
    }
});
