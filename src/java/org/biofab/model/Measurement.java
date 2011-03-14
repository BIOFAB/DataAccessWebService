/*
 * 
 * 
 */

package org.biofab.model;

/**
 *
 * @author cesarr
 */
public class Measurement
{
    protected int       id;
    protected String    time;
    protected float     value;

    
    public Measurement(int id, String time, float value)
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
    public String getTime()
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
