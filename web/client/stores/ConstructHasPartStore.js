/*
 * File: ConstructHasPartStore.js
 * 
 *
 */

ConstructHasPartStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        ConstructHasPartStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'constructHasPartStore',
            //root: 'constructhaspart',
            url: '../constructpart',
            params: {
                format: 'json'
            },
            autoLoad: true,
            fields: [
                {
                    name: 'id',
                    allowBlank: false,
                    type: 'int'
                },
                {
                    name: 'construct',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'part',
                    allowBlank: false,
                    type: 'string'
                }
            ]
        }, cfg));
    }
});
new ConstructHasPartStore();