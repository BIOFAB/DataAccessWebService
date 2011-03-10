/*
 * 
 * 
 */

package org.biofab.model;

/**
 *
 * @author cesarr
 */
public class ConstructPerformance
{
    protected Read[]    reads;


    
    public ConstructPerformance(Read[] reads)
    {
        this.reads = reads;
    }
    

    /**
     * @return the _reads
     */
    public Read[] getReads()
    {
        return reads;
    }

    /**
     * @param reads the _reads to set
     */
    public void setReads(Read[] reads)
    {
        this.reads = reads;
    }
}
