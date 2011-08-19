/*
 *
 * File: PilotProjectPanel.js
 * 
 */

Ext.define('TerminatorPanel',{
    extend: 'Ext.panel.Panel',
    title: 'Terminator Library',
    layout: 'absolute',
    tpl: '',
    closable: true,
    autoScroll: true,
    id: 'terminatorPanel',
    collectionRecord: null,
    
    constructor: function() {
        
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
                floating: false,
                //shadowOffset: 6,
                items: [
                    {
                        xtype: 'panel',
                        itemId: 'textPanel',
                        layout: 'fit',
                        region: 'center',
                        split: true,
                        height: 350,
                        items: [
                            {
                                xtype: 'textarea',
                                itemId: 'textArea'
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        //title: 'Sequences and Performance',
                        region: 'south',
                        layout: 'fit',
                        height: 450,
                        split: true,
                        items: [
                            {
                                xtype: 'grid',
                                store: store,
                                //height: 300,
                                stripeRows: true,
                                columnLines: true,
                                itemId: 'terminatorsGridPanel',
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
                                    itemId: 'terminatorsGridToolbar',
                                    items: [
                                        {
                                            xtype: 'label',
                                            itemId: "terminatorTableLabel",
                                            style: {fontWeight:'bold'},
                                            text: 'Sequences and Performance'
                                        },
                                        {
                                            xtype: 'tbfill'
                                        },
                                        {
                                            xtype: 'button',
                                            text: 'Export',
                                            tooltip: 'Export all the terminator information in JSON format',
                                            itemId: 'terminatorsGridExportButton',
                                            handler: this.terminatorsExportButtonHandler
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }
        ];
        
        this.callParent();
    },

    //
    //  Public Methods
    //
    
    setCollectionRecord: function(collectionRecord)
    {
            var description = null;

            this.collectionRecord = collectionRecord;
            description = collectionRecord.get('description');
            this.getComponent('centerPanel').getComponent('textPanel').getComponent('textArea').setValue(description);
    },

    terminatorsExportButtonHandler: function(button, event)
    {
        var exportWindow = window.open(WEB_SERVICE_BASE_URL + 'terminators?format=json');
        exportWindow.alert("Use File/Save As in the menu bar to save this document.");
        exportWindow.scrollbars.visible = true;
    }
});
