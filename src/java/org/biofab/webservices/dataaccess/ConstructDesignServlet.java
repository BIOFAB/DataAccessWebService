package org.biofab.webservices.dataaccess;


import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

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

import org.sbolstandard.libSBOLj.SBOLutil;
import org.sbolstandard.libSBOLj.DnaComponent;
import org.sbolstandard.libSBOLj.Library;
import org.sbolstandard.libSBOLj.SBOLservice;

@SuppressWarnings("serial")
@WebServlet(name="ConstructDesignServlet", urlPatterns={"/construct/design/*"})
public class ConstructDesignServlet extends DataAccessServlet
{
    public ConstructDesignServlet()
    {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Statement       statement = null;
        String          constructID = request.getParameter("id");
        String          format = request.getParameter("format");
        String          responseString = null;
        RichSequence    richSequence = null;
        Feature         feature = null;
        //String[]        features;
        String          newID;
        String          componentID;
        Namespace       ns = RichObjectFactory.getDefaultNamespace();
        ResultSet       resultSet = null;
        Statement       featuresStatement;
        ResultSet       features;
        String          designID;

        if(constructID != null && constructID.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                featuresStatement = _connection.createStatement();
                resultSet = statement.executeQuery("SELECT design.id, design.biofab_id, design.description, design.dna_sequence FROM design WHERE UPPER(design.biofab_id) = '" + constructID.toUpperCase() + "'");
      
                while (resultSet.next())
                {
                    String dnaSequence = resultSet.getString("dna_sequence");
                    designID = resultSet.getString("id");
                    
                    try
                    {
                        features = featuresStatement.executeQuery("SELECT feature.description, feature.genbank_type, design_feature.start, design_feature.stop FROM design_feature, feature WHERE design_feature.design_id = '" + designID + "' AND feature.id = design_feature.feature_id AND feature.display_in_view = TRUE ORDER BY design_feature.start ASC");
                        richSequence = RichSequence.Tools.createRichSequence(constructID, DNATools.createDNA(dnaSequence));
                        addFeatures(richSequence, features);
                        //addComment(richSequence, "The genetic constructs used here are taken or composed from available, well known genetic elements.  At this time BIOFAB staff have not yet taken care to define the precise functional boundaries of these genetic elements.  Thus, for example, a part labeled as a \"promoter\" may include sequences encoding all or part of a 5' UTR downstream of a transcription start site. And so on. Part of the mission of the BIOFAB is to define compatible sets of genetic objects with precise and composable boundaries. Such well engineered parts will be noted once available.");
                        richSequence.setCircular(true);
                        
                        if(format != null && format.equalsIgnoreCase("genbank"))
                        {
                            response.setContentType("text/plain");
                            RichSequence.IOTools.writeGenbank(response.getOutputStream(), richSequence, ns);
                        }
                        else
                        {
                            if(format.equalsIgnoreCase("insd"))
                            {
                                response.setContentType("text/xml");
                                RichSequence.IOTools.writeINSDseq(response.getOutputStream(), richSequence, ns);
                            }
                            else
                            {
                                if(format.equalsIgnoreCase("sboljson"))
                                {
                                    SBOLservice service = new SBOLservice();
                                    Library library = service.createLibrary("Test", "Test","Test");

                                    DnaComponent dnaComponent = SBOLutil.readRichSequence(richSequence);
                                    library = service.addDnaComponentToLibrary(dnaComponent, library);
                                    String sbolString = SBOLutil.toJson(library);

                                    if(sbolString != null && sbolString.length() > 0)
                                    {
                                        response.setContentType("text/plain");
                                        response.getWriter().println(sbolString);
                                    }
                                    else
                                    {
                                      //TODO  Manage null case
                                    }

                                    //this.textError(response, "SBOL JSON is under development.");
                                }
                                else
                                {
                                    if(format.equalsIgnoreCase("sbolrdf"))
                                    {
//                                        SBOLservice service = new SBOLservice();
//                                        Library library = service.createLibrary("Test", "Test","Test");
//
//
//                                        DnaComponent dnaComponent = SBOLutil.readRichSequence(richSequence);
//                                        library = service.addDnaComponentToLibrary(dnaComponent, library);
//                                        String sbolString = SBOLutil.toRDF(library);
//
//                                        if(sbolString != null && sbolString.length() > 0)
//                                        {
//                                            response.setContentType("text/plain");
//                                            response.getWriter().println(sbolString);
//                                        }
//                                        else
//                                        {
//                                          //TODO  Manage null case
//                                        }

                                        this.textError(response, "SBOL RDF is under development.");
                                    }
                                    else
                                    {
                                        if(format != null && format.equalsIgnoreCase("seq"))
                                        {
                                            response.setContentType("text/plain");
                                            response.getWriter().println(richSequence.seqString());
                                        }
                                        else
                                        {
                                            response.setContentType("text/plain");
                                            RichSequence.IOTools.writeGenbank(response.getOutputStream(), richSequence, ns);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        textError(response, "Error while fetching data: " + ex.getMessage());
                    }
                }
            }
            catch (SQLException ex)
            {
                textError(response, "Error while fetching data: " + ex.getMessage());
            }
            finally
            {
                try
                {
                    _connection.close();
                }
                catch (SQLException ex)
                {
                    textError(response, "Error while fetching data: " + ex.getMessage());
                }
            }

        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/plain");
        response.getWriter().println("You have contacted the BIOFAB Construct Design Web Service.");
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
            featureType = features.getString("genbank_type");
            noteValue = features.getString("description");
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
