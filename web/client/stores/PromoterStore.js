/*
 * File: PromoterStore.js
 * 
 */

PromoterStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        PromoterStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'promoterStore',
            autoLoad: false,
            //groupField: 'displayId',
            fields: [
                {
                    name: 'collectionId',
                    allowBlank: false,
                    type: 'int'
                },
                {
                    name: 'displayId',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'type',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'description',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'dnaSequence',
                    type: 'string'
                },
                {
                    name: 'geneExpressionPerCell',
                    type: 'float'
                },
                {
                    name: 'geneExpressionPerCellSD',
                    type: 'float'
                },
                {
                    name: 'constructId',
                    type: 'string'
                }
            ]
        }, cfg));
    }
});
new PromoterStore();