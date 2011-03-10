/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.model;

/**
 *
 * @author cesarr
 */
public class Instrument
{
    protected String        _type;
    protected String        _make;
    protected String        _model;

    /**
     * @return the _type
     */
    public String getType()
    {
        return _type;
    }

    /**
     * @param type the _type to set
     */
    public void setType(String type)
    {
        this._type = type;
    }

    /**
     * @return the _make
     */
    public String getMake()
    {
        return _make;
    }

    /**
     * @param make the _make to set
     */
    public void setMake(String make)
    {
        this._make = make;
    }

    /**
     * @return the _model
     */
    public String getModel()
    {
        return _model;
    }

    /**
     * @param model the _model to set
     */
    public void setModel(String model)
    {
        this._model = model;
    }
}
