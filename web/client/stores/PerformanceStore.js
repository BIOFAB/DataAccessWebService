/*
 * File: PerformanceStore.js
 */

PerformanceStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        PerformanceStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'performanceStore',
            root: 'performance',
            url: 'data/performance.json',
            autoLoad: true,
            fields: [
                {
                    name: 'id',
                    allowBlank: false,
                    type: 'int'
                },
                {
                    name: 'constructid',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'bulkfluorescence',
                    allowBlank: false
                }
            ]
        }, cfg));
    }
});
new PerformanceStore();