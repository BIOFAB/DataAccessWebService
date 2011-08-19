
package org.biofab.datasheets.model;

/**
 *
 * @author cesarr
 */
public class Part
{
   int          collectionID;
   int          id;
   String       biofabID;
   String       description;
   String       biofabType;
   String       dnaSequence;

    public Part(int collectionID, int id, String biofabID, String description, String biofabType, String dnaSequence)
    {
        this.collectionID = collectionID;
        this.id = id;
        this.biofabID = biofabID;
        this.description = description;
        this.biofabType = biofabType;
        this.dnaSequence = dnaSequence;
    }

    public String getBiofabID()
    {
        return biofabID;
    }

    public String getBiofabType()
    {
        return biofabType;
    }

    public int getCollectionID()
    {
        return collectionID;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDnaSequence()
    {
        return dnaSequence;
    }

    public int getId()
    {
        return id;
    }
}
