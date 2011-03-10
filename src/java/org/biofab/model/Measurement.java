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
    protected String    _time;
    protected float     _value;

    /**
     * @return the _time
     */
    public String getTime()
    {
        return _time;
    }

    /**
     * @param time the _time to set
     */
    public void setTime(String time)
    {
        this._time = time;
    }

    /**
     * @return the _value
     */
    public float getValue()
    {
        return _value;
    }

    /**
     * @param value the _value to set
     */
    public void setValue(float value)
    {
        this._value = value;
    }
}
