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
                                    xtype: 'button',
                                    text: 'Genbank',
                                    ref: '../../../designPanelGenbankButton'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'tbtext',
                                    text: 'Fetching Design...',
                                    hidden: true,
                                    ref: '../../../designPanelText'
                                }
                            ]
                        }
                    },
                    {
                        xtype: 'panel',
                        title: 'Performance',
                        layout: 'vbox',
                        ref: '../performancePanel',
                        tbar: {
                            xtype: 'toolbar',
                            ref: '../../performPanelToolbar',
                            items: [
                                {
                                    xtype: 'button',
                                    text: 'CSV',
                                    ref: '../../../performPanelCSVButton',
                                    id: 'performancePanelCSVButton'
                                },
                                {
                                    xtype: 'tbfill'
                                },
                                {
                                    xtype: 'tbtext',
                                    text: 'Fetching Data...',
                                    hidden: true,
                                    ref: '../../../performPanelText'
                                }
                            ]
                        }
                    }
                ]
            }
        ];
        
        DatasheetPanelUi.superclass.initComponent.call(this);
    }
});
