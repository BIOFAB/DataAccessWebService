/*
 * File: CollectionStore.js
 * 
 */

CollectionStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        CollectionStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'aCollectionsStore',
            root: 'collections',
            url: 'data/collections.json',
            autoLoad: true,
            fields: [
                {
                    name: 'id',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'name',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'version',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'description',
                    allowBlank: false,
                    type: 'string'
                }
            ]
        }, cfg));
    }
});
new CollectionStore();