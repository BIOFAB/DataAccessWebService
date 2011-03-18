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
    protected Read[]            reads;
    protected CytometerRead[]   cytometerReads;


    
    public ConstructPerformance(Read[] reads, CytometerRead[] cytometerReads)
    {
        this.reads = reads;
        this.cytometerReads = cytometerReads;
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

    public CytometerRead[] getCytometerReads()
    {
        return cytometerReads;
    }
}
