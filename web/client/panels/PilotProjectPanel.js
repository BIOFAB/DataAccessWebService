/*
 *
 * File: PilotProjectPanel.js
 * 
 */

PilotProjectPanel = Ext.extend(PilotProjectPanelUi,{
    collectionRecord: null,

    initComponent: function()
    {
        PilotProjectPanel.superclass.initComponent.call(this);
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
    }
});
