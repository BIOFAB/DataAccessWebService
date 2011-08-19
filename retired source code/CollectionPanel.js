/*
 *
 * File: CollectionPanel.js
 * 
 */

CollectionPanel = Ext.extend(CollectionPanelUi,{
    collectionRecord: null,

    initComponent: function()
    {
        CollectionPanel.superclass.initComponent.call(this);
    },

    //
    //
    //  Public Methods
    //
    
    setCollectionRecord: function(collectionRecord)
    {
            var id = null;
            var name = null;
            var description = null;

            this.collectionRecord = collectionRecord;
            id = collectionRecord.get("id");
            name = collectionRecord.get('name');
            description = collectionRecord.get('description');
            this.setTitle(name);
            this.centerPanelRef.setTitle(name + ' Collection');
            this.collectionTextAreaRef.setValue(description);
    }
});
