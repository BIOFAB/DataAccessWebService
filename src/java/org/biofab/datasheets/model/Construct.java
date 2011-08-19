

package org.biofab.datasheets.model;

/**
 *
 * @author cesarr
 */

import java.util.Date;


public class Construct
{
    protected int                     id;
    protected String                  biofabID;
    protected ConstructPerformance    performance;


    public Construct(int id, String biofabID, ConstructPerformance performance)
    {
        //TODO: manage the boundary cases

        this.id = id;

        if(biofabID != null && biofabID.length() > 0)
        {
            this.biofabID = biofabID;
        }
        else
        {
            //Throw exception
        }

        if(performance != null)
        {
            this.performance = performance;
        }
        else
        {
            //Throw exception
        }
    }


    /**
     * @return the id
     */
    public int getID()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
//    public void setID(int id)
//    {
//        this.id = id;
//    }

    /**
     * @return the biofabID
     */
    public String getBiofabID()
    {
        return biofabID;
    }

    /**
     * @param biofabID the biofabID to set
     */
//    public void setBiofabID(String biofabID)
//    {
//        this.biofabID = biofabID;
//    }

    /**
     * @return the performance
     */
    public ConstructPerformance getPerformance()
    {
        return performance;
    }

    /**
     * @param performance the performance to set
     */
    public void setPerformance(ConstructPerformance performance)
    {
        this.performance = performance;
    }
}
