/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.model;

/**
 *
 * @author cesarr
 */

import java.util.Date;

public class Collection 
{
    protected int       id;
    protected String    biofabID;
    protected String    name;
    protected String    version;
    protected String    releaseStatus;
    protected Date      releaseDate;
    protected String    description;

    public Collection(int id, String biofabID, String name, String version, String releaseStatus, Date releaseDate, String description)
    {
        this.id = id;
        this.biofabID = biofabID;
        this.name = name;
        this.version = version;
        this.releaseStatus = releaseStatus;
        this.releaseDate = releaseDate;
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public String getBiofabID()
    {
        return biofabID;
    }
    
    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public Date getReleaseDate()
    {
        return releaseDate;
    }

    public String getReleaseStatus()
    {
        return releaseStatus;
    }

    public String getDescription()
    {
        return description;
    }
}
