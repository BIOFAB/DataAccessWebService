/*
 *
 * File: DatasheetPanel.ui.js
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
                                    ref: '../../../designPanelExportButton'
                                }
                            ]
                        }
                    }
//                    {
//                        xtype: 'panel',
//                        title: 'Performance',
//                        layout: 'table',
//                        autoscroll: true,
//                        ref: '../performancePanel',
//                        tbar: {
//                            xtype: 'toolbar',
//                            ref: '../../performPanelToolbar',
//                            items: [
//                                {
//                                    xtype: 'button',
//                                    text: 'Export',
//                                    ref: '../../../performPanelExportButton'
//                                },
//                                {
//                                    xtype: 'tbfill'
//                                },
//                                {
//                                    xtype: 'tbtext',
//                                    text: 'Fetching Data...',
//                                    hidden: true,
//                                    ref: '../../../performPanelText'
//                                }
//                            ]
//                        },
//                        layoutConfig:{columns: 2},
//                        items:[
//                                {
//                                    xtype:'panel',
//                                    title: 'Bulk Gene Expression  (Under Development)',
//                                    layout: 'auto',
//                                    width: 500,
//                                    height: 300,
//                                    items:[
//                                            {
//                                              xtype:'container',
//                                              ref: '../../../temporalPlotContainer',
//                                              autoEl: 'div',
//                                              layout: 'auto'
//                                            }
//                                    ]
//                                },
//                                {
//                                    xtype:'panel',
//                                    title: 'Data Acquisition Information',
//                                    width: 300,
//                                    height: 300
//                                },
//                                {
//                                    xtype:'panel',
//                                    title: 'Gene Expression per Cell  (Under Development)',
//                                    width: 500,
//                                    height: 300,
//                                    items:[
//                                            {
//                                              xtype:'container',
//                                              ref: '../../../histogramContainer',
//                                              autoEl: 'div',
//                                              layout: 'auto'
//
//                                            }
//                                    ]
//                                },
//                                {
//                                    xtype:'panel',
//                                    title: 'Data Acquisition Information',
//                                    width: 300,
//                                    height: 300
//                                }
//                        ]
//                    }
                ]
            }
        ];
        
        DatasheetPanelUi.superclass.initComponent.call(this);
    }
});
