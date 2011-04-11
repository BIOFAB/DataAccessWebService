
/*
 *
 * 
 * 
 */

RandomPromoterPanel = Ext.extend(RandomPromoterPanelUi,
{
    collectionRecord: null,

    initComponent: function()
    {
        RandomPromoterPanel.superclass.initComponent.call(this);
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
