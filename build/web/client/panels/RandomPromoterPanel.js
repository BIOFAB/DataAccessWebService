
/*
 *
 * 
 * 
 */

Ext.define('RandomPromoterPanel',
{
    extend: 'Ext.panel.Panel',
    title: 'Random Promoter Library',
    layout: 'absolute',
    tpl: '',
    closable: true,
    autoScroll: true,
    id: 'randomPromoterPanel',
    collectionRecord: null,
    
    constructor: function() {
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
                floating: false,
                shadowOffset: 6,
                //autoShow: true,
                draggable: false,
                items: [
                    {
                        xtype: 'panel',
                        itemId: 'textPanel',
                        layout: 'fit',
                        region: 'center',
                        split: true,
//                        height: 300,
                        items: [
                            {
                                xtype: 'textarea',
                                itemId: 'textArea'
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
    
    showInfo: function(collectionRecord)
    {
            var description = null;

            this.collectionRecord = collectionRecord;
            description = collectionRecord.get('description');
            this.getComponent('centerPanel').getComponent('textPanel').getComponent('textArea').setValue(description);
    }
});
