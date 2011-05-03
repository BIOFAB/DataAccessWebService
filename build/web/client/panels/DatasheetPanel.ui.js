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
                        xtype: 'tabpanel',
                        title: 'Performance',
                        //layout: 'border',
                        activeTab: 0,
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
                                layout: 'border',
                                //width: 400,
                                //height: 500,
                                //ref: '../../bulkGeneExpressionPanelRef',
                                //region: 'center',
                                //split: true,
                                items:[
                                    {
                                        xtype:'panel',
                                        title: '',
                                        layout: 'auto',
                                        //width: 400,
                                        //height: 500,
                                        ref: '../../../bulkGeneExpressionPanelRef',
                                        region: 'center',
                                        split: true
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
                                            title: 'Data Acquisition Information',
                                            layout: 'fit',
                                            //width: 450,
                                            ref: '../../../bulkGeneExpressionInfoPanelRef',
                                            region: 'center',
                                            split: true,
                                            items:[
                                                {
                                                    xtype: 'textarea',
                                                    value: 'Data acquisition information will be provided in this space in an upcoming release of the Data Access Client.\n'
                                                            +'Optical density and replicate measurements will also be made available.\n'
                                                            +'If you click on the "Export" button you can examine the optical density data in JSON format.',
                                                    hidden: false,
                                                    ref: '../../../bulkGeneExpressionTextAreaRef'
                                                }
                                            ]

                                        }
                                        ]
                                    }
                                    
                                ]
                            },
                            {
                                xtype:'panel',
                                title: 'Gene Expression per Cell',
                                layout: 'border',
                                //width: 400,
                                //height: 500,
                                //ref: '../../../bulkGeneExpressionPanelRef',
                                //region: 'center',
                                //split: true,
                                items:[
                                    {
                                        xtype:'panel',
                                        title: '',
                                        layout: 'auto',
                                        ref:'../../../geneExpressionPerCellPanelRef',
                                        region: 'center',
                                        split: true,
                                        bbar: {
                                                xtype: 'toolbar',
                                                ref: '../../../../geneExpressionPerCellPanelToolbar',
                                                height: 30,
                                                items: [
                                                    {
                                                        xtype: 'combo',
                                                        ref: '../../../../../geneExpressionPerCellComboBox',
                                                        store: [
                                                            'Fluorescence Histogram',
                                                            'Fluorescence vs. Forward Scatter',
                                                            'Fluorescence vs. Side Scatter'
                                                        ],
                                                        //autoSelect: true,
                                                        typeAhead: true,
                                                        triggerAction: 'all',
                                                        selectOnFocus:true,
                                                        width:250,
                                                        forceSelection:true,
                                                        allowBlank: false,
                                                        editable: false,
                                                        emptyText: 'Select a plot type...'
                                                        //tooltip: 'Select a plot type to display'
                                                    },
                                                    {
                                                       xtype:'tbfill'
                                                    },
                                                    {
                                                        xtype: 'tbtext',
                                                        ref: '../../../../../dataDisplayedTextRef',
                                                        text: 'Events',
                                                        hidden: false,
                                                        tooltip: 'The number of events displayed in the plot'
                                                    },
                                                    {
                                                        xtype: 'tbseparator',
                                                        ref: '../../../../../geneExpPerCellSeparator'
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        text: 'Show All',
                                                        disabled: true,
                                                        tooltip: 'Display all the events',
                                                        ref: '../../../../../showAllEventsButtonRef'
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
                                            title: 'Data Acquisition Information',
                                            layout: 'fit',
                                            //width: 450,
                                            ref: '../../geneExpressionPerCellInfoPanelRef',
                                            region: 'center',
                                            split: true,
                                            items:[
                                                {
                                                    xtype: 'textarea',
                                                    value: 'Data acquisition information will be provided in this space in an upcoming release of the Data Access Client.\n'
                                                           +'Replicate measurements will also be made available.\n',
                                                    hidden: false,
                                                    ref: '../../../geneExpressionPerCellTextAreaRef'
                                                }
                                            ]

                                        }
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
