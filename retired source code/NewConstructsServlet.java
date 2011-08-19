package org.biofab.datasheets;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.sbolstandard.libSBOLj.experiment.DnaComponent;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.StrandedFeature;
import org.biojava.bio.symbol.RangeLocation;

import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.SimpleComment;
import org.biojavax.SimpleNote;
import org.biojavax.SimpleRichAnnotation;
import org.biojavax.bio.seq.RichSequence;


@WebServlet(name="NewConstructsServlet", urlPatterns={"/new/constructs/*"})
public class NewConstructsServlet extends DataAccessServlet
{
    String                  _collectionId;
    String                  _constructId;
    String                  _queryString;
    
    @Override
    public void init()
    {

    }

    @Override 
    public void destroy()
    {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        _collectionId = request.getParameter("collectionid");
        _constructId = request.getParameter("id");
        _format = request.getParameter("format");
        _request = request;
        _response = response;
        
        //TODO stronger validation of plasmidId
        
        if(_constructId == null)
        {
            fetchConstructs();
        }
        else
        {
            fetchConstructDesign();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        textError(response, "Post requests are not serviced by the Constructs web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        textError(response, "Put requests are not serviced by the Constructs web service");
    }

    // Utility Functions
    protected void fetchConstructs()
    {
        String                  responseString = null;
        String                  query = null;
        ResultSet               resultSet;
        String                  biofabId;
        String                  description;
        int                     collectionId;
        
        ArrayList<DnaComponent> dnaComponents = null;
        DnaComponent            dnaComponent = null;
        
        dnaComponents = new ArrayList<DnaComponent>();
        query = "SELECT * FROM public.construct_view";
        resultSet = fetchResultSet(query);
        
        try
        {
            while (resultSet.next())
            {
                collectionId = 0;
                biofabId = resultSet.getString("biofab_id");
                description = resultSet.getString("description");
            
                dnaComponent = new DnaComponent(collectionId, biofabId, "", description, "PLASMID", true, null);
                dnaComponents.add(dnaComponent);
            }

            if(_format == null || _format.equalsIgnoreCase("json"))
            {
               responseString = generateJSON(dnaComponents.toArray());
               this.textSuccess(_response, responseString);
            }
            else
            {
//                if(format.equalsIgnoreCase("csv"))
//                {
//                   responseString = generateCSV(dnaComponents);
//                   this.textSuccess(response, responseString);
//                }
//                else
//                {
//                   responseString = generateJSON(dnaComponents.toArray());
//                   this.textSuccess(response, responseString);
//                }
            }
        }
        catch (SQLException ex)
        {
            if(_format != null && _format.length() > 0)
            {
                if(_format.equalsIgnoreCase("json"))
                {
                    jsonError(_response, "Error while fetching data: " + ex.getMessage());

                }
                else
                {
                    textError(_response, "Error while fetching data: " + ex.getMessage());
                }
            }
            else
            {
                textError(_response, "Error while fetching data: " + ex.getMessage());
            }
        }
    }
    
    protected void fetchConstructDesign()
    {
        Statement       statement = null;
        RichSequence    richSequence = null;
        Namespace       ns = RichObjectFactory.getDefaultNamespace();
        ResultSet       resultSet = null;
        Statement       featuresStatement;
        ResultSet       annotationsResultSet;
        String          biofabId;
        String          dnaSequence;
        String          query;
        int             fetchSize;
        boolean         hasSequence = false;

        if(_constructId != null && _constructId.length() > 0)
        {
            query = "SELECT * FROM private.plasmid_sequence_view WHERE UPPER(biofab_id) = '" + _constructId.toUpperCase() + "'";
            resultSet = fetchResultSet(query);
            
            try
            {
                while (resultSet.next())
                {
                    biofabId = resultSet.getString("biofab_id");
                    dnaSequence = resultSet.getString("nucleotides");
                    query = "SELECT * FROM private.plasmid_annotation_view WHERE plasmid_biofab_id = '" + biofabId + "'";

                    try
                    {
                        richSequence = RichSequence.Tools.createRichSequence(biofabId, DNATools.createDNA(dnaSequence));
                        annotationsResultSet = fetchResultSet(query);
                        addFeatures(richSequence, annotationsResultSet);
                        richSequence.setCircular(true);

                        if(_format == null || _format.equalsIgnoreCase("genbank"))
                        {
                            _response.setContentType("text/plain");
                            RichSequence.IOTools.writeGenbank(_response.getOutputStream(), richSequence, ns);
                        }
                        else
                        {

                            if(_format.equalsIgnoreCase("insd"))
                            {
                                _response.setContentType("text/xml");
                                RichSequence.IOTools.writeINSDseq(_response.getOutputStream(), richSequence, ns);
                            }
                            else
                            {
                               _response.setContentType("text/plain");
                                RichSequence.IOTools.writeGenbank(_response.getOutputStream(), richSequence, ns);
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        textError(_response, "Error while fetching data: " + ex.getMessage());
                    }
                    
                    hasSequence = true;
                }
                
                if(hasSequence == false)
                {
                    textError(_response, "No sequence is available for " + _constructId);
                }
            }
            catch (SQLException ex)
            {
                textError(_response, "Error while fetching data: " + ex.getMessage());
            }
        }
    }
    
    protected void addComment(RichSequence seq, String comment)
    {
        seq.addComment(new SimpleComment(comment, 0));
    }

    protected void addFeatures(RichSequence sequence, ResultSet features) throws SQLException, BioException
    {
        String featureType;
        String noteType;
        String noteValue;
        int start;
        int stop;

        while (features.next())
        {
            featureType = features.getString("feature_type");
            noteValue = features.getString("feature_description");
            start = features.getInt("start");
            stop = features.getInt("stop");

            if(featureType.equalsIgnoreCase("CDS"))
            {
                noteType = "gene";
            }
            else
            {
                noteType = "label";
            }

            SimpleRichAnnotation annotation = new SimpleRichAnnotation();

            annotation.addNote(new SimpleNote(RichObjectFactory.getDefaultOntology().getOrCreateTerm(noteType), noteValue, 0));
            StrandedFeature.Template featureTemplate = new StrandedFeature.Template();
            featureTemplate.annotation = annotation;
            featureTemplate.location = new RangeLocation(start, stop);
            featureTemplate.source = "BIOFAB";
            featureTemplate.strand = StrandedFeature.POSITIVE;
            featureTemplate.type = featureType;
            sequence.createFeature(featureTemplate);
        }
    }
}
