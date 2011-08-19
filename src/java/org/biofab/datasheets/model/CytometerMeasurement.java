/*
 * 
 * 
 */

package org.biofab.datasheets.model;

import java.sql.Time;

/**
 *
 * @author cesarr
 */
public class CytometerMeasurement
{
    protected long      id;
    protected float     fluorescence;
    protected float     forwardScatter;
    protected float     sideScatter;

    public CytometerMeasurement(long id, float fluorescence, float forwardScatter, float sideScatter)
    {
        this.id = id;
        this.fluorescence = fluorescence;
        this.forwardScatter = forwardScatter;
        this.sideScatter = sideScatter;
    }

    public long getId()
    {
        return id;
    }
    
    public float getFluorescence()
    {
        return fluorescence;
    }

    public float getForwardScatter()
    {
        return forwardScatter;
    }

    public float getSideScatter()
    {
        return sideScatter;
    }
}
