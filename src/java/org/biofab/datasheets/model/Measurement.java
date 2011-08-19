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
public class Measurement
{
    protected int       id;
    protected long      time;
    protected float     value;

    
    public Measurement(int id, long time, float value)
    {
        this.id = id;
        this.time = time;
        this.value = value;
    }
    
    /**
     * @return th ID
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * @return the time
     */
    public long getTime()
    {
        return time;
    }

    /**
     * @return the _value
     */
    public float getValue()
    {
        return value;
    }
}
