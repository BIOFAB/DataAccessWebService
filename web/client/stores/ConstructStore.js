/*
 * File: ConstructStore.js
 * 
 * This file was generated by Ext Designer version xds-1.0.3.0.
 * http://www.extjs.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

ConstructStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        ConstructStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'constructStore',
            root: 'constructs',
            url: WEB_SERVICE_BASE_URL + 'constructs?projectid=1&format=json',
            autoLoad: true,
            fields: [
                {
                    name: 'id',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'description',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'mid_log_phase_fluorescence',
                    allowBlank: false,
                    type: 'float',
                    sortDir: 'DESC'
                },
                {
                    name: 'chassis',
                    type: 'string',
                    allowBlank: false
                },
                {
                    name: 'media',
                    type: 'string',
                    allowBlank: false
                }
            ]
        }, cfg));
    }
});
new ConstructStore();