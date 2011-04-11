/*
 * 
 * 
 */

RandomPromoterPanelUi = Ext.extend(Ext.Panel, {
    title: 'Random Promoter Library',
    layout: 'absolute',
    tpl: '',
    closable: true,
    autoScroll: true,
    id: 'randomPromoterPanel',
    initComponent: function() {
        this.items = [
            {
                xtype: 'panel',
                title: 'Random Promoter Library',
                height: 300,
                width: 600,
                layout: 'border',
                itemId: 'centerPanel',
                x: 25,
                y: 25,
                ref: 'centerPanelRef',
                floating: true,
                shadowOffset: 6,
                //autoShow: true,
                draggable: false,
                items: [
                    {
                        xtype: 'panel',
                        layout: 'fit',
                        region: 'center',
                        split: true,
//                        height: 300,
                        items: [
                            {
                                xtype: 'textarea',
                                itemId: 'collectionTextArea',
                                ref: '../../collectionTextAreaRef'
                            }
                        ]
                    }
//                    {
//                        xtype: 'tabpanel',
//                        activeTab: 0,
//                        region: 'south',
//                        height: 500,
//                        split: true,
//                        items:[
//                            {
//                                xtype: 'panel',
//                                title: 'Design',
//                                layout: 'auto',
//                                html: '<p></p><h4>Under development...</h4>'
//                            },
//                            {
//                                xtype: 'panel',
//                                title: 'Performance',
//                                layout: 'auto',
//                                html: '<p></p><h4>Under development...</h4>'
//                            }
//                        ]
//                    }
                ]
            }
        ];
        
        PilotProjectPanelUi.superclass.initComponent.call(this);
    }
});
