/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.daws.model;

/**
 *
 * @author cesarr
 */
public class Instrument
{
    protected String        type;
    protected String        make;
    protected String        model;


    public Instrument(String type, String make, String model)
    {
        this.type = type;
        this.make = make;
        this.model = model;
    }


    /**
     * @return the _type
     */
    public String getType()
    {
        return type;
    }


    /**
     * @return the _make
     */
    public String getMake()
    {
        return make;
    }


    /**
     * @return the _model
     */
    public String getModel()
    {
        return model;
    }
}
