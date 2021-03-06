/*
 * 
 * 
 */

ModularPromoterPanelUi = Ext.extend(Ext.Panel, {
    title: 'Modular Promoter Library',
    layout: 'absolute',
    tpl: '',
    closable: true,
    autoScroll: true,
    id: 'modularPromoterPanel',
    initComponent: function() {
        this.items = [
            {
                xtype: 'panel',
                title: '',
                height: 800,
                layout: 'border',
                width: 600,
                itemId: 'centerPanel',
                x: 25,
                y: 25,
//                ref: 'centerPanelRef',
                floating: false,
                shadowOffset: 6,
                autoShow: true,
                draggable: false,
                region:'center',
                items: [
                    {
                        xtype: 'panel',
                        itemId: 'designPanel',
                        title: 'Design',
                        layout: 'fit',
                        region: 'north',
                        split: true,
                        height:250,
                        items: [
                            {
                                xtype: 'textarea',
                                itemId: 'collectionTextArea'
                            },
                            
                        ]
                    },
                    {
                        xtype: 'panel',
                        title: 'Performance',
                        itemId: 'performancePanel',
                        layout: 'auto',
                        height: 450,
                        region:'center',
                        split: true
                    },
                    {
                        xtype:'panel',
                        title: 'Notes',
                        itemId: 'notesPanel',
                        layout: 'fit',
                        height: 100,
                        region: 'south',
                        split: true,
                        items:[
                            {
                                xtype: 'textarea',
                                value: 'Each bar indicates the performance of a part in the library.\n' +
                                       'Hover the mouse over a bar to see the identifier of a part.\n',
                                hidden: false
                            }
                        ]

                    }
                ]
            }
        ];
        PilotProjectPanelUi.superclass.initComponent.call(this);
    }
});
