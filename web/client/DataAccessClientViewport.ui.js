/*
 * File: DataAccessClientViewport.ui.js
 * 
 * This file was generated by Ext Designer version xds-1.0.3.0.
 * http://www.extjs.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

DataAccessClientViewportUi = Ext.extend(Ext.Viewport, {
    layout: 'border',
    id: 'dataAccessClientViewport',
    initComponent: function() {
        this.items = [
            {
                xtype: 'container',
                region: 'west',
                width: 400,
                layout: 'border',
                split: true,
                ref: 'componentsContainer',
                id: 'componentsContainer',
                items: [
                    {
                        xtype: 'grid',
                        title: 'Parts',
                        store: 'partStore',
                        region: 'center',
                        split: true,
                        columnLines: true,
                        stripeRows: true,
                        ref: '../partsGridPanel',
                        id: 'partsGridPanel',
                        selModel: new Ext.grid.RowSelectionModel({
                            singleSelect: true
                        }),
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'id',
                                header: 'Identifier',
                                sortable: true,
                                width: 80,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Type',
                                sortable: true,
                                width: 100,
                                dataIndex: 'type'
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Description',
                                sortable: true,
                                width: 175,
                                dataIndex: 'description'
                            }
                        ],
                        tbar: {
                            xtype: 'toolbar',
                            items: [
                                {
                                    xtype: 'button',
                                    text: 'CSV',
                                    ref: '../../../partsGridCSVButton',
                                    id: 'partsGridCSVButton'
                                }
                            ]
                        }
                    },
                    {
                        xtype: 'grid',
                        title: 'Constructs',
                        store: 'constructDisplayStore',
                        region: 'south',
                        split: true,
                        height: 300,
                        stripeRows: true,
                        columnLines: true,
                        ref: '../constructsGridPanel',
                        id: 'constructsGridPanel',
                        selModel: new Ext.grid.RowSelectionModel({
                            singleSelect: true
                        }),
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'id',
                                header: 'Identifier',
                                sortable: true,
                                width: 100,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Description',
                                sortable: true,
                                width: 200,
                                dataIndex: 'description',
                                editable: false
                            },
                            {
                                xtype: 'numbercolumn',
                                header: 'Mid Log Phase Fluorescence',
                                sortable: true,
                                width: 175,
                                align: 'left',
                                editable: false,
                                dataIndex: 'mid_log_phase_fluorescence'
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'chassis',
                                header: 'Strain',
                                sortable: true,
                                width: 100,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Media',
                                sortable: true,
                                width: 100,
                                dataIndex: 'media',
                                editable: false
                            }
                        ],
                        tbar: {
                            xtype: 'toolbar',
                            items: [
                                {
                                    xtype: 'button',
                                    text: 'CSV',
                                    ref: '../../../constructsGridCSVButton',
                                    id: 'constructsGridCSVButton'
                                }
                            ]
                        }
                    }
                ]
            },
            {
                xtype: 'tabpanel',
                activeTab: 0,
                region: 'center',
                split: true,
                ref: 'infoTabPanel',
                id: 'infoTabPanel'
            }
        ];
        DataAccessClientViewportUi.superclass.initComponent.call(this);
    }
});
