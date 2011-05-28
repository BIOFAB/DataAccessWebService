/*
 * File: DataAccessClientViewport.ui.js
 * 
 */

DataAccessClientViewportUi = Ext.extend(Ext.Viewport, {
    layout: 'border',
    id: 'dataAccessClientViewport',
    initComponent: function() {
        this.items = [
//            {
//                xtype: 'container',
//                region: 'north',
//                width: 100,
//                layout: 'fit',
//                height: 30,
//                id: 'northContainer',
//                items: [
//                    {
//                        xtype: 'toolbar',
//                        items: [
//                            {
//                                xtype: 'tbfill'
//                            },
//                            {
//                                xtype: 'button',
//                                text: 'Help',
//                                ref: '../../helpButtonRef',
//                                id: 'helpButton'
//                            }
//                        ]
//                    }
//                ]
//            },
            {
                xtype: 'container',
                region: 'west',
                width: 500,
                layout: 'border',
                split: true,
                id: 'inventoryContainer',
                items: [
                    {
                        xtype: 'grid',
                        store: 'aCollectionsStore',
                        region: 'north',
                        split: true,
                        height: 150,
                        //autoExpandColumn: 5,
                        minColumnWidth: 60,
                        ref: '../collectionsGridPanel',
                        id: 'collectionsGridPanel',
                        selModel: new Ext.grid.RowSelectionModel({

                        }),
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'biofabID',
                                header: 'Identifier',
                                sortable: true,
                                width: 60,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'chassis',
                                header: 'Chassis',
                                sortable: true,
                                width: 60,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'name',
                                header: 'Name',
                                sortable: true,
                                width: 150,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Version',
                                sortable: true,
                                editable: false,
                                width: 60,
                                dataIndex: 'version'
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Release Status',
                                sortable: true,
                                editable: false,
                                width: 100,
                                dataIndex: 'releaseStatus'
                            },
                            {
                                xtype: 'gridcolumn',
                                header: 'Release Date',
                                sortable: true,
                                editable: false,
                                width: 100,
                                dataIndex: 'releaseDate'
                            }

                        ],
                        tbar: {
                            xtype: 'toolbar',
                            id: 'collectionsToolbar',
                            items: [
                                {
                                    xtype: 'label',
                                    html: '<b>Collections</b>'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Export',
                                    tooltip: 'Export collection information in JSON format.',
                                    ref: '../../../collectionsGridExportButtonRef',
                                    id: 'collectionsGridExportButton'
                                }
                            ]
                        }
                    },
                    {
                        xtype: 'grid',
                        store: 'partStore',
                        height: 400,
                        columnLines: true,
                        stripeRows: true,
                        //autoExpandColumn: 2,
                        split: true,
                        region: 'center',
                        ref: '../partsGridPanel',
                        id: 'partsGridPanel',
                        tbar: {
                            xtype: 'toolbar',
                            items: [
                                {
                                    xtype: 'label',
                                    id: "partsLabel",
                                    ref: "../../../partsLabel",
                                    style: {fontWeight:'bold'},
                                    text: 'Parts'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Promoters',
                                    tooltip: 'Show only the promoters',
                                    ref: '../../../promotersButtonRef',
                                    id: 'promotersButton'
                                },
                                {
                                    xtype: 'tbseparator'
                                },
                                {
                                    xtype: 'button',
                                    text: '5\' UTR',
                                    tooltip: 'Show only 5\' UTRs',
                                    ref: '../../../fiveUTRButtonRef',
                                    id: 'fiveUTRButton'
                                },
                                {
                                    xtype: 'tbseparator'
                                },
                                {
                                    xtype: 'button',
                                    text: 'CDS',
                                    tooltip: 'Show only the CDSs',
                                    ref: '../../../cdsButtonRef',
                                    id: 'cdsButton'
                                },
                                {
                                    xtype: 'tbseparator'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Show All',
                                    tooltip: 'Show all the parts',
                                    ref: '../../../showAllPartsButtonRef',
                                    id: 'showAllPartsButton'
                                },
                                {
                                    xtype: 'tbseparator'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Export',
                                    tooltip: 'Export all the parts in JSON format',
                                    ref: '../../../partsGridExportButton',
                                    id: 'partsGridExportButton'
                                }
                            ]
                        },
                        selModel: new Ext.grid.RowSelectionModel({
                            singleSelect: true
                        }),
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'displayID',
                                header: 'Identifier',
                                sortable: true,
                                width: 80,
                                editable: false
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'type',
                                header: 'Part Type',
                                sortable: true,
                                width: 100,
                                editable: false
                                
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'description',
                                header: 'Description',
                                sortable: true,
                                width: 150
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'measurementLabel',
                                header: 'Measurement Type',
                                sortable: true,
                                width: 150,
                                align: 'left',
                                editable: false
                            },
                            {
                                xtype: 'numbercolumn',
                                dataIndex: 'measurementValue',
                                header: 'Measurement Value',
                                sortable: true,
                                width: 150,
                                align: 'left',
                                editable: false,
                                format: '0,000'
                            }
                        ]
                    },
                    {
                        xtype: 'grid',
                        //title: 'Constructs',
                        store: 'constructDisplayStore',
                        height: 200,
                        stripeRows: true,
                        columnLines: true,
                        region: 'south',
                        split: true,
                        //anchor: '100%, 33%',
                        //flex: 0.4,
                        ref: '../constructsGridPanel',
                        id: 'constructsGridPanel',
                        selModel: new Ext.grid.RowSelectionModel({
                            singleSelect: true
                        }),
                        columns: [
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'biofab_id',
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
                                header: 'Bulk Gene Expression',
                                sortable: true,
                                width: 175,
                                align: 'left',
                                editable: false,
                                dataIndex: 'bulk_gene_expression',
                                format: '0,000'
                            },
                            {
                                xtype: 'numbercolumn',
                                header: 'Standard Deviation',
                                sortable: true,
                                width: 175,
                                align: 'left',
                                editable: false,
                                dataIndex: 'bulk_gene_expression_sd',
                                format: '0,000.0'
                            },
                            {
                                xtype: 'numbercolumn',
                                header: 'Gene Expression per Cell',
                                sortable: true,
                                width: 175,
                                align: 'left',
                                editable: false,
                                dataIndex: 'gene_expression_per_cell',
                                format: '0,000'
                            },
                            {
                                xtype: 'numbercolumn',
                                header: 'Standard Deviation',
                                sortable: true,
                                width: 175,
                                align: 'left',
                                editable: false,
                                dataIndex: 'gene_expression_per_cell_sd',
                                format: '0,000.0'
                            }
                        ],
                        tbar: {
                            xtype: 'toolbar',
                            ref: '../../constructsGridToolbar',
                            id: 'constructsGridToollbar',
                            items: [
                                {
                                    xtype: 'label',
                                    id: "constructsLabel",
                                    ref: "../../../constructsLabel",
                                    style: {fontWeight:'bold'},
                                    text: 'Constructs'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Show All',
                                    tooltip: 'Show all the constructs',
                                    ref: '../../../showAllConstructsButtonRef',
                                    id: 'showAllConstructsButton'
                                },
                                {
                                    xtype: 'tbseparator'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Export',
                                    tooltip: 'Export all the construct information in CSV format',
                                    ref: '../../../constructsGridExportButton',
                                    id: 'constructsGridExportButton'
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
