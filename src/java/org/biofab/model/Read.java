

package org.biofab.model;

/**
 *
 * @author cesarr
 */

public class Read
{
    protected int           id;
    protected String        date;
    protected String        typeCode;
    protected String        typeName;
    protected Instrument    instrument;
    protected Measurement[] measurements;



    public Read(String date, String typeCode, String typeName, Instrument instrument, Measurement[] measurements)
    {
        this.date = date;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.instrument = instrument;
        this.measurements = measurements;
    }
    

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }


    /**
     * @return the date
     */
    public String getDate()
    {
        return date;
    }


    /**
     * @return the typeCode
     */
    public String getTypeCode()
    {
        return typeCode;
    }


    /**
     * @return the _typeName
     */
    public String getTypeName()
    {
        return typeName;
    }


    /**
     * @return the _instrument
     */
    public Instrument getInstrument()
    {
        return instrument;
    }


    /**
     * @return the _measurements
     */
    public Measurement[] getMeasurements()
    {
        return measurements;
    }
}
