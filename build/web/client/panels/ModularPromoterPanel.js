/*
 *
 * File: PilotProjectPanel.js
 * 
 */

ModularPromoterPanel = Ext.extend(ModularPromoterPanelUi,
{
    collectionRecord: null,

    initComponent: function()
    {
        ModularPromoterPanel.superclass.initComponent.call(this);
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
