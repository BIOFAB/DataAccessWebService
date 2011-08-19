/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.datasheets.model;

/**
 *
 * @author cesarr
 */
public class ConstructPartRelationship
{
    String constructID;
    String partID;


    public ConstructPartRelationship(String constructID, String partID)
    {
        this.constructID = constructID;
        this.partID = partID;
    }

    public String getConstructID()
    {
        return constructID;
    }

    public String getPartID()
    {
        return partID;
    }
}
