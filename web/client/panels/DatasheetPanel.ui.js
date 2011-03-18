/*
 *
 *
 *
 */

DatasheetPanelUi = Ext.extend(Ext.Panel, {
    layout: 'fit',
    closable: true,
    initComponent: function() {
        this.items = [
            {
                xtype: 'tabpanel',
                activeTab: 1,
                ref: 'datasheetTabPanel',
                items: [
                    {
                        xtype: 'panel',
                        title: 'Design',
                        layout: 'fit',
                        ref: '../designPanel',
                        tbar: {
                            xtype: 'toolbar',
                            ref: '../../designPanelToolbar',
                            items: [
                                {
                                    xtype: 'tbtext',
                                    text: 'Fetching Design...',
                                    hidden: true,
                                    ref: '../../../designPanelText'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Export',
                                    ref: '../../../designPanelExportButtonRef'
                                }
                            ]
                        }
                    },
                    {
                        xtype: 'panel',
                        title: 'Performance',
                        layout: 'border',
                        autoscroll: true,
                        ref: '../performancePanelRef',
                        tbar: {
                            xtype: 'toolbar',
                            ref: '../../performancePanelToolbar',
                            items: [
                                {
                                    xtype: 'tbtext',
                                    text: 'Fetching Data...',
                                    hidden: true,
                                    ref: '../../../performancePanelTextRef'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Export',
                                    ref: '../../../performancePanelExportButtonRef'
                                }
                            ]
                        },
                        items:[
                                {
                                    xtype:'panel',
                                    title: 'Bulk Gene Expression',
                                    layout: 'auto',
                                    width: 400,
                                    //height: 400,
                                    ref: '../../bulkGeneExpressionPanelRef',
                                    region: 'center',
                                    split: true
                                },
                                {
                                    xtype:'panel',
                                    title: 'Gene Expression per Cell',
                                    width: 400,
                                    //height: 400,
                                    ref:'../../geneExpressionPerCellPanelRef',
                                    region: 'east',
                                    split: true,
                                    bbar: {
                                            xtype: 'toolbar',
                                            ref: '../../../geneExpressionPerCellPanelToolbar',
                                            items: [
                                                {
                                                    xtype: 'tbtext',
                                                    text: 'Events Displayed',
                                                    hidden: false,
                                                    ref: '../../../../dataDisplayedTextRef'
                                                },
                                                {
                                                    xtype: 'tbfill'
                                                },
                                                {
                                                    xtype: 'button',
                                                    text: 'Display All',
                                                    disabled: true,
                                                    tooltip: 'Display all the events',
                                                    ref: '../../../../displayAllEventsButtonRef'
                                                }
                                            ]
                                    }
                                },
                                {
                                    xtype:'panel',
                                    title: '',
                                    height: 200,
                                    region: 'south',
                                    split: true,
                                    layout: 'border',
                                    items:[
                                    {
                                        xtype:'panel',
                                        title: 'Data Acquisition Notes',
                                        layout: 'fit',
                                        width: 450,
                                        ref: '../../notesPanelRef',
                                        region: 'west',
                                        split: true,
                                        items:[
                                            {
                                                xtype: 'textarea',
                                                value: 'Data acquisition notes are displayed here.',
                                                hidden: false,
                                                ref: '../../../notesTextAreaRef'
                                            }
                                        ]

                                    },
                                    {
                                        xtype:'panel',
                                        title: 'Data Acquisition Information List',
                                        ref:'../../infoListPanelRef',
                                        region: 'center',
                                        split: true,
                                        items:[
                                            
                                        ]
                                    }
                                    ]
                                }
                        ]
                    }
                ]
            }
        ];

        DatasheetPanelUi.superclass.initComponent.call(this);
    }
});
