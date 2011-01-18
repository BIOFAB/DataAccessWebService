/*
 * File: PartStore.js
 * 
 * This file was generated by Ext Designer version xds-1.0.3.0.
 * http://www.extjs.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

PartStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        PartStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'partStore',
            root: 'annotatedparts',
            url: WEB_SERVICE_BASE_URL + 'annotatedparts?projectid=1&format=json',
            autoLoad: true,
            fields: [
                {
                    name: 'id',
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
                    name: 'sequence',
                    type: 'string'
                }
            ]
        }, cfg));
    }
});
new PartStore();