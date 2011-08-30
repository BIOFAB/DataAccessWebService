/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.daws.model;

/**
 *
 * @author cesarr
 */

public class Terminator
{
    int         id;
    String      biofabID;
    String      description;
    float       terminationEfficiency;
    float       terminationEfficiencySD;
    String      dnaSequence;

    public Terminator(int id, String biofabID, String description, float terminationEfficiency, float terminationEfficiencySD, String dnaSequence)
    {
        this.id = id;
        this.biofabID = biofabID;
        this.description = description;
        this.terminationEfficiency = terminationEfficiency;
        this.terminationEfficiencySD = terminationEfficiencySD;
        this.dnaSequence = dnaSequence;
    }

    public String getBiofabID()
    {
        return biofabID;
    }

    public String getDescription()
    {
        return description;
    }

    public int getId()
    {
        return id;
    }

    public float getTerminationEfficiency()
    {
        return terminationEfficiency;
    }

    public float getTerminationEfficiencySD()
    {
        return terminationEfficiencySD;
    }

    public String dnaSequence()
    {
        return dnaSequence;
    }
}
