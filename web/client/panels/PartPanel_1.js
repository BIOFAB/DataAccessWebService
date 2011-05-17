/*
 *
 * 
 * 
 */

PartPanel = Ext.extend(PartPanelUi,{
    partRecord: null,

    initComponent: function()
    {
        PartPanel.superclass.initComponent.call(this);
    },

    //
    //  Public Methods
    //
    
    setPartRecord: function(partRecord)
    {
            var dnaSequence = null;
            var biofabID = null;

            this.partRecord = partRecord;
            biofabID = partRecord.get('biofabID');
            this.setTitle(biofabID);
            this.sequencePanel.setTitle('DNA Sequence for ' + biofabID);
            this.performancePanel.setTitle('Performance for ' + biofabID);
            dnaSequence = partRecord.get('dnaSequence');
            this.sequenceTextArea.setValue(dnaSequence);
    }
});
