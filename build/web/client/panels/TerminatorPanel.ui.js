/*
 *
 *
 *
 */

TerminatorPanelUi = Ext.extend(Ext.Panel, {
    title: 'Terminator Library',
    layout: 'absolute',
    tpl: '',
    closable: true,
    autoScroll: true,
    id: 'terminatorPanel',
    initComponent: function() {
        var store = new Ext.data.JsonStore({
            autoDestroy: true,
            autoLoad: true,
            url: '../terminators?format=json',
            storeId: 'terminatorStore',
            idProperty: 'id',
            fields: [
                {
                    name: 'id',
                    allowBlank: false,
                    type: 'int'
                },
                {
                    name: 'biofabID',
                    type: 'string',
                    allowBlank: false
                },
                {
                    name: 'description',
                    allowBlank: false,
                    type: 'string'
                },
                {
                    name: 'terminationEfficiency',
                    allowBlank: false,
                    type: 'float',
                    sortDir: 'DESC'
                },
                {
                    name: 'terminationEfficiencySD',
                    allowBlank: false,
                    type: 'float'
                },
                {
                    name: 'dnaSequence',
                    allowBlank: false,
                    type: 'string'
                }
            ]
        });

        this.items = [
            {
                xtype: 'panel',
                title: 'Terminator Library',
                height: 800,
                width: 600,
                layout: 'border',
                itemId: 'centerPanel',
                x: 25,
                y: 25,
                ref: 'centerPanelRef',
                floating: false,
                shadowOffset: 6,
                items: [
                    {
                        xtype: 'panel',
                        layout: 'fit',
                        region: 'center',
                        split: true,
                        height: 350,
                        items: [
                            {
                                xtype: 'textarea',
                                itemId: 'collectionTextArea',
                                ref: '../../collectionTextAreaRef'
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        title: 'Sequences and Performance',
                        region: 'south',
                        layout: 'fit',
                        height: 450,
                        split: true,
                        items: [
                            {
                                xtype: 'grid',
                                //title: 'Terminator Sequences and Performance Summary',
                                //store: 'terminatorStore',
                                store: store,
                                height: 300,
                                stripeRows: true,
                                columnLines: true,
                                //region: 'south',
                                //split: true,
                                //anchor: '100%, 33%',
                                //flex: 0.4,
                                ref: '../terminatorsGridPanelRef',
                                selModel: new Ext.grid.RowSelectionModel({
                                    singleSelect: true
                                }),
                                columns: [
                                    {
                                        xtype: 'gridcolumn',
                                        dataIndex: 'biofabID',
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
                                        header: 'Termination Efficiency',
                                        sortable: true,
                                        width: 175,
                                        align: 'left',
                                        editable: false,
                                        dataIndex: 'terminationEfficiency',
                                        format: '0,000'
                                    },
                                    {
                                        xtype: 'numbercolumn',
                                        header: 'Standard Deviation',
                                        sortable: true,
                                        width: 175,
                                        align: 'left',
                                        editable: false,
                                        dataIndex: 'terminationEfficiencySD',
                                        format: '0,000.0'
                                    },
                                    {
                                        xtype: 'gridcolumn',
                                        header: 'DNA Sequence',
                                        sortable: true,
                                        width: 200,
                                        align: 'left',
                                        editable: false,
                                        dataIndex: 'dnaSequence'
                                    }
                                ],
                                tbar: {
                                    xtype: 'toolbar',
                                    ref: '../../../terminatorsGridToolbarRef',
                                    id: 'terminatorsGridToolbar',
                                    items: [
                                        {
                                            xtype: 'tbfill'
                                        },
                                        {
                                            xtype: 'button',
                                            text: 'Export',
                                            tooltip: 'Export all the terminator information in JSON format',
                                            ref: '../../../../terminatorsGridExportButtonRef',
                                            id: 'terminatorsGridExportButton'
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }
        ];
        TerminatorPanelUi.superclass.initComponent.call(this);
    }
});
