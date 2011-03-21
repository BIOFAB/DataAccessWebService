/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.model;

/**
 *
 * @author cesarr
 */
public class Collection 
{
    protected int       id;
    protected String    biofabID;
    protected String    name;
    protected String    version;
    protected String    description;

    public Collection(int id, String biofabID, String name, String version, String description)
    {
        this.id = id;
        this.biofabID = biofabID;
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String getBiofabID() {
        return biofabID;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
