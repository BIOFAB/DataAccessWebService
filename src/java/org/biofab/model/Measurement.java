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
    protected String    time;
    protected float     value;

    
    public Measurement(String time, float value) 
    {
        this.time = time;
        this.value = value;
    }

    
    /**
     * @return the _time
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
