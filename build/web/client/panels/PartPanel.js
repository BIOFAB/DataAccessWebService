/*
 *
 *
 *
 */

PartPanel = Ext.extend(Ext.Panel, {
    title: 'Part',
    layout: 'border',
    tpl: '',
    closable: true,
    autoScroll: false,

    //Data Members
    partRecord: null,

    //Function Members
    initComponent: function() {
        this.items = [
            {
                xtype: 'panel',
                title: 'DNA Sequence',
                height: 150,
                layout: 'fit',
                ref: 'sequencePanel',
                region: 'north',
                split: true,
                items: [
                    {
                        xtype: 'textarea',
                        ref: '../sequenceTextArea',
                        readOnly: true
                    }
                ]
            },
            {
                xtype: 'panel',
                title: 'Performance',
                layout: 'fit',
                region: 'center',
                //height: 400,
                split: true,
                ref: 'performancePanel',
                html: '<p>Performance data will be available in an upcoming release of the Data Access Client.</p>'
//              items: []
            }
        ];

        PartPanel.superclass.initComponent.call(this);
    },
    
    setPartRecord: function(partRecord)
    {
            var dnaSequence = null;
            var biofabID = null;

            this.partRecord = partRecord;
            biofabID = partRecord.get('displayID');
            this.setTitle(biofabID);
            this.sequencePanel.setTitle('DNA Sequence of ' + biofabID);
            this.performancePanel.setTitle('Performance of ' + biofabID);
            dnaSequence = partRecord.get('dnaSequence');
            this.sequenceTextArea.setValue(dnaSequence);
    }
});
