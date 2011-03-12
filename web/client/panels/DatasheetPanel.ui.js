/*
 * File: DatasheetPanel.ui.js
 * 
 * This file was generated by Ext Designer version xds-1.0.3.2.
 * http://www.extjs.com/products/designer/
 *
 * This file was manually exported.
 */

DatasheetPanelUi = Ext.extend(Ext.Panel, {
    layout: 'fit',
    closable: true,
    initComponent: function() {
        this.items = [
            {
                xtype: 'tabpanel',
                activeTab: 0,
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
                        ref: '../performPanelRef',
                        tbar: {
                            xtype: 'toolbar',
                            ref: '../../performPanelToolbar',
                            items: [
                                {
                                    xtype: 'tbtext',
                                    text: 'Fetching Data...',
                                    hidden: true,
                                    ref: '../../../performPanelTextRef'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'button',
                                    text: 'Export',
                                    ref: '../../../performPanelExportButtonRef'
                                }
                            ]
                        },
                        items:[
                                {
                                    xtype:'panel',
                                    title: 'Bulk Gene Expression (Under Development)',
                                    layout: 'auto',
                                    width: 400,
                                    //height: 400,
                                    ref: '../../bulkGeneExpressionPanelRef',
                                    region: 'west',
                                    split: true
                                },
                                {
                                    xtype:'panel',
                                    title: 'Gene Expression per Cell (Under Development)',
                                    width: 400,
                                    //height: 400,
                                    ref:'../../geneExpressionPerCellPanelRef',
                                    region: 'center',
                                    split: true
                                },
                                {
                                    xtype:'panel',
                                    title: 'Data Acquisition Information',
                                    width: 800,
                                    height: 200,
                                    region: 'south',
                                    split: true
                                }
                        ]
                    }
                ]
            }
        ];

        DatasheetPanelUi.superclass.initComponent.call(this);
    }
});
