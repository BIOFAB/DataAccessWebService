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
                title: 'Modular Promoter Library',
                height: 300,
                layout: 'border',
                width: 600,
                itemId: 'centerPanel',
                x: 25,
                y: 25,
                ref: 'centerPanelRef',
                floating: true,
                shadowOffset: 6,
                autoShow: true,
                draggable: false,
                items: [
                    {
                        xtype: 'panel',
                        //title: 'Design',
                        layout: 'fit',
                        region: 'center',
                        split: true,
                        items: [
                            {
                                xtype: 'textarea',
                                itemId: 'collectionTextArea',
                                //height: '300',
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
//                                layout: 'table',
//                                layoutConfig:{
//                                    columns: 6
//                                },
//                                defaults:{
//                                    height: 50,
//                                    width: 100
//                                },
//                                items: [
//                                    {
//                                       colspan: 6,
//                                       width: 595,
//                                       height: 150,
//                                       html: '<img width=210 height=112 src="data/modular_promoter_library_design.png">'
//
//                                    },
//                                    {
//                                       colspan: 6,
//                                       width: 595,
//                                       height: 300,
//                                       html: '<p>In the third column of the table, the -35 motifs, -10 motifs and transcription start sites are highlighted in the original 5 promoters. Module 1 contains -35 motifs and UP elements, where applicable. Module 2 is the spacer region between -10 and -35 motifs. Module 3 contains -10 regions and spacers between -10 and +1. The recombination of the 3 modules from the 5 original promoters yields a total of 125 new promoters. The Modular Promoters were characterized in a construct with the BIOFAB’s Expression Operating Unit (EOU) .  The EOU is a standard gene expression cassette under development at the BIOFAB.  The host plasmids contain a p15A origin of replication (medium copy), Bujard 5’ UTR (5 Prime Untranslated Region), and Super Folder GFP (sfGFP) as a reporter.  The constructs are assayed in the BW25113 strain in EZ rich MOPS media (Technova) at 37 degrees Celcius.  GFP fluorescence is measured with a Biotek plate reader and Guava flow cytometer.  Bulk gene expression is the slope of the linear portion of a rate plot of background-subtracted OD versus RFU (OD600, 0.2 to 0.55; roughly corresponds to the promoter activity).  Gene expression per cell is the mean fluorescence value of approximately 2000 events in the flow cytometer. The standard deviations are derived from three or more replicate experiments.</p>'
//                                    }
//                                ]
//
//                            },
//                            {
//                                xtype: 'panel',
//                                title: 'Performance',
//                                layout: 'auto'
//                            }
//                        ]
//                    }
                ]
            }
        ];
        PilotProjectPanelUi.superclass.initComponent.call(this);
    }
});
