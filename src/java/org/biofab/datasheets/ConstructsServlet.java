package org.biofab.datasheets;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Time;
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

import org.sbol.libsbol.json.DnaComponent;

import org.biofab.datasheets.model.Construct;
import org.biofab.datasheets.model.ConstructPerformance;
import org.biofab.datasheets.model.Read;
import org.biofab.datasheets.model.CytometerRead;
import org.biofab.datasheets.model.Instrument;
import org.biofab.datasheets.model.Measurement;
import org.biofab.datasheets.model.CytometerMeasurement;


@WebServlet(name="ConstructsServlet", urlPatterns={"/constructs/*"})
public class ConstructsServlet extends DataAccessServlet
{
    String                  _collectionID;
    String                  _constructId;
    String                  _details;
    String                  _queryString;
    String                  _collectionId;
    
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
        
        //TODO stronger validation of constructId
        
        if(_constructId == null)
        {
            fetchConstructs();
        }
        else
        {
            fetchConstructDesign();
        }

    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//    throws ServletException, IOException
//    {
//        _collectionID = request.getParameter("collectionid");
//        _constructId = request.getParameter("id");
//        _format = request.getParameter("format");
//        _details = request.getParameter("details");
//        _response = response;
//
//        //TODO Do regex validation of collectionid
//        if(_collectionID != null)
//        {
//            _queryString = "SELECT * FROM construct_performance_summary_view "
//                    + "WHERE construct_performance_summary_view.collection_id = " + _collectionID
//                    + "ORDER BY construct_performance_summary_view.bulk_gene_expression DESC;";
//
//            fetchConstructs();
//        }
//        else
//        {
//           if(_collectionID == null && _constructId == null)
//           {
//                _queryString = "SELECT * FROM construct_performance_summary_view "
//                    + "ORDER BY construct_performance_summary_view.bulk_gene_expression DESC;";
//
//                fetchConstructs();
//           }
//           else
//           {
//               if(_constructId != null && _details == null)
//               {
//                    fetchConstructDesign();
//               }
//               else
//               {
//                   if(_constructId != null && _details != null)
//                   {
//                       if(_details.equalsIgnoreCase("true"))
//                       {
//                          this.fetchConstructPerformanceDetails();
//                       }
//                       else
//                       {
//                           fetchConstructDesign();
//                       }
//                   }
//               }
//           }
//        }
//    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        textError(response, "Post requests are not serviced by the Constructs web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        textError(response, "Put requests are not serviced by the Constructs web service");
    }
    

    /*********************
     * 
     * Utility Functions
     * 
     *********************/
    
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
        query = "SELECT * FROM dev.construct_view";
        resultSet = fetchResultSet(query);
        
        try
        {
            while (resultSet.next())
            {
                collectionId = 0;
                biofabId = resultSet.getString("biofab_id");
                description = resultSet.getString("description");
            
                dnaComponent = new DnaComponent(biofabId, "", description, "PLASMID");
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
            query = "SELECT * FROM dev.construct_sequence_view WHERE UPPER(biofab_id) = '" + _constructId.toUpperCase() + "'";
            resultSet = fetchResultSet(query);
            
            try
            {
                while (resultSet.next())
                {
                    biofabId = resultSet.getString("biofab_id");
                    dnaSequence = resultSet.getString("nucleotides");
                    query = "SELECT * FROM dev.construct_annotation_view WHERE construct_biofab_id = '" + biofabId + "'";

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
    
//    protected void fetchConstructs()
//    {
//        String responseString = null;
//        Statement statement = null;
//        ResultSet resultSet = null;
//
//        try
//        {
//            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
//            statement = _connection.createStatement();
//            resultSet = statement.executeQuery(_queryString);
//
//            if(_format.equalsIgnoreCase("json"))
//            {
//               responseString = generateJSON(resultSet);
//               this.textSuccess(_response, responseString);
//            }
//            else
//            {
//               responseString = generateCSV(resultSet);
//               this.textSuccess(_response, responseString);
//            }
//
//        }
//        catch (Exception ex)
//        {
//            if(_format != null && _format.length() > 0)
//            {
//                if(_format.equalsIgnoreCase("json"))
//                {
//                    jsonError(_response, "Error while fetching data: " + ex.getMessage());
//
//                }
//                else
//                {
//                    textError(_response, "Error while fetching data: " + ex.getMessage());
//                }
//            }
//        }
//        finally
//        {
//            try
//            {
//                _connection.close();
//            }
//            catch (SQLException ex)
//            {
//                if(_format != null && _format.length() > 0)
//                {
//                    if(_format.equalsIgnoreCase("json"))
//                    {
//                        jsonError(_response, "Error while fetching data: " + ex.getMessage());
//                    }
//                    else
//                    {
//                        textError(_response, "Error while fetching data: " + ex.getMessage());
//                    }
//                }
//                else
//                {
//                    textError(_response, "Error while fetching data: " + ex.getMessage());
//                }
//            }
//        }
//    }
//
//    protected void fetchConstructDesign()
//    {
//        Statement       statement = null;
//        String          responseString = null;
//        RichSequence    richSequence = null;
//        Feature         feature = null;
//        //String[]        features;
//        String          newID;
//        String          componentID;
//        Namespace       ns = RichObjectFactory.getDefaultNamespace();
//        ResultSet       resultSet = null;
//        Statement       featuresStatement;
//        ResultSet       features;
//        String          designID;
//        
//        try
//         {
//                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
//                statement = _connection.createStatement();
//                featuresStatement = _connection.createStatement();
//                resultSet = statement.executeQuery("SELECT design.id, design.biofab_id, design.description, design.dna_sequence FROM design WHERE UPPER(design.biofab_id) = '" + _constructId.toUpperCase() + "'");
//
//                while (resultSet.next())
//                {
//                    String dnaSequence = resultSet.getString("dna_sequence");
//                    designID = resultSet.getString("id");
//
//                    try
//                    {
//                        features = featuresStatement.executeQuery("SELECT feature.description, feature.genbank_type, design_feature.start, design_feature.stop FROM design_feature, feature WHERE design_feature.design_id = '" + designID + "' AND feature.id = design_feature.feature_id AND feature.display_in_view = TRUE ORDER BY design_feature.start ASC");
//                        richSequence = RichSequence.Tools.createRichSequence(_constructId, DNATools.createDNA(dnaSequence));
//                        addFeatures(richSequence, features);
//                        //addComment(richSequence, "The genetic constructs used here are taken or composed from available, well known genetic elements.  At this time BIOFAB staff have not yet taken care to define the precise functional boundaries of these genetic elements.  Thus, for example, a part labeled as a \"promoter\" may include sequences encoding all or part of a 5' UTR downstream of a transcription start site. And so on. Part of the mission of the BIOFAB is to define compatible sets of genetic objects with precise and composable boundaries. Such well engineered parts will be noted once available.");
//                        richSequence.setCircular(true);
//
//                        if(_format != null && _format.equalsIgnoreCase("genbank"))
//                        {
//                            _response.setContentType("text/plain");
//                            RichSequence.IOTools.writeGenbank(_response.getOutputStream(), richSequence, ns);
//                        }
//                        else
//                        {
//                            if(_format.equalsIgnoreCase("insd"))
//                            {
//                                _response.setContentType("text/xml");
//                                RichSequence.IOTools.writeINSDseq(_response.getOutputStream(), richSequence, ns);
//                            }
//                            else
//                            {
//                                if(_format.equalsIgnoreCase("sboljson"))
//                                {
////                                    SBOLservice service = new SBOLservice();
////                                    Library library = service.createLibrary("Test", "Test","Test");
////
////                                    DnaComponent dnaComponent = SBOLutil.readRichSequence(richSequence);
////                                    library = service.addDnaComponentToLibrary(dnaComponent, library);
////                                    String sbolString = SBOLutil.toJson(library);
////
////                                    if(sbolString != null && sbolString.length() > 0)
////                                    {
////                                        response.setContentType("text/plain");
////                                        response.getWriter().println(sbolString);
////                                    }
////                                    else
////                                    {
////                                      //TODO  Manage null case
////                                    }
//
//                                    this.textError(_response, "SBOL JSON is under development.");
//                                }
//                                else
//                                {
//                                    if(_format.equalsIgnoreCase("sbolrdf"))
//                                    {
////                                        SBOLservice service = new SBOLservice();
////                                        Library library = service.createLibrary("Test", "Test","Test");
////
////
////                                        DnaComponent dnaComponent = SBOLutil.readRichSequence(richSequence);
////                                        library = service.addDnaComponentToLibrary(dnaComponent, library);
////                                        String sbolString = SBOLutil.toRDF(library);
////
////                                        if(sbolString != null && sbolString.length() > 0)
////                                        {
////                                            response.setContentType("text/plain");
////                                            response.getWriter().println(sbolString);
////                                        }
////                                        else
////                                        {
////                                          //TODO  Manage null case
////                                        }
//
//                                        this.textError(_response, "SBOL RDF is under development.");
//                                    }
//                                    else
//                                    {
//                                        if(_format != null && _format.equalsIgnoreCase("seq"))
//                                        {
//                                            _response.setContentType("text/plain");
//                                            _response.getWriter().println(richSequence.seqString());
//                                        }
//                                        else
//                                        {
//                                            _response.setContentType("text/plain");
//                                            RichSequence.IOTools.writeGenbank(_response.getOutputStream(), richSequence, ns);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    catch (Exception ex)
//                    {
//                        textError(_response, "Error while fetching data: " + ex.getMessage());
//                    }
//                }
//            }
//            catch (SQLException ex)
//            {
//                textError(_response, "Error while fetching data: " + ex.getMessage());
//            }
//            finally
//            {
//                try
//                {
//                    _connection.close();
//                }
//                catch (SQLException ex)
//                {
//                    textError(_response, "Error while fetching data: " + ex.getMessage());
//                }
//            }
//    }

    protected void fetchConstructPerformanceDetails()
    {
        Statement               statement = null;
        Statement               cytoStatement = null;
        String                  queryString = null;
        Construct               construct = null;
        ConstructPerformance    performance = null;
        ArrayList<Read>         reads;
        Read[]                  readArray = null;
        Read                    read = null;
        int                     constructID;
        String                  constructBiofabID = null;
        int                     readID;
        String                  readDate = null;
        String                  readTypeCode = null;
        String                  readTypeName = null;
        String                  instrumentType = null;
        String                  instrumentMake = null;
        String                  instrumentModel = null;
        Instrument              instrument = null;
        Measurement[]           measurements = null;
        String                  plateWell = null;

        // TODO Do RegExp validation on constructIDParam
        if(_constructId != null && _constructId.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                cytoStatement = _connection.createStatement();
                queryString = "SELECT * FROM construct_read_view WHERE UPPER(construct_read_view.construct_biofab_id) = '" + _constructId.toUpperCase() + "' ORDER BY construct_read_view.read_id ASC";
                String cytoQueryString = "SELECT * FROM construct_cytometer_read_view WHERE UPPER(construct_cytometer_read_view.construct_biofab_id) = '" + _constructId.toUpperCase() + "' ORDER BY construct_cytometer_read_view.read_id ASC";
                ResultSet resultSet = statement.executeQuery(queryString);
                ResultSet cytoResultSet = cytoStatement.executeQuery(cytoQueryString);
                reads = new ArrayList<Read>();
                ArrayList<CytometerRead>cytoReads = new ArrayList<CytometerRead>();

                while (resultSet.next())
                {
                    if(construct == null)
                    {
                        constructID = resultSet.getInt("construct_id");
                        constructBiofabID = resultSet.getString("construct_biofab_id");
                        construct = new Construct(constructID, constructBiofabID, null);
                    }

                    readID = resultSet.getInt("read_id");
                    readDate = resultSet.getString("read_date");
                    readTypeCode = resultSet.getString("read_type_code");
                    readTypeName = resultSet.getString("read_type_name");
                    instrumentType = resultSet.getString("instrument_type");
                    instrumentMake = resultSet.getString("instrument_make");
                    instrumentModel = resultSet.getString("instrument_model");
                    instrument = new Instrument(instrumentType, instrumentMake, instrumentModel);
                    plateWell = resultSet.getString("plate_well");

                    statement = _connection.createStatement();
                    queryString = "SELECT measurement.id, measurement.time, measurement." + plateWell.toLowerCase() + " AS value FROM measurement WHERE measurement.read_id = " + String.valueOf(readID) + " ORDER BY measurement.time ASC";
                    ResultSet measurementResultSet = statement.executeQuery(queryString);
                    measurements = this.createMeasurementArray(measurementResultSet);
                    read = new Read(readID, readDate, readTypeCode, readTypeName, instrument, measurements);
                    reads.add(read);
                }

                while (cytoResultSet.next())
                {
                    if(construct == null)
                    {
                        int cytoConstructID = cytoResultSet.getInt("construct_id");
                        String cytoConstructBiofabID = cytoResultSet.getString("construct_biofab_id");
                        construct = new Construct(cytoConstructID, cytoConstructBiofabID, null);
                    }

                    int cytoReadID = cytoResultSet.getInt("read_id");
                    String cytoReadDate = cytoResultSet.getString("read_date");
                    String cytoReadTypeCode = cytoResultSet.getString("read_type_code");
                    String cytoReadTypeName = cytoResultSet.getString("read_type_name");
                    String cytoInstrumentType = cytoResultSet.getString("instrument_type");
                    String cytoInstrumentMake = cytoResultSet.getString("instrument_make");
                    String cytoInstrumentModel = cytoResultSet.getString("instrument_model");
                    Instrument cytoInstrument = new Instrument(cytoInstrumentType, cytoInstrumentMake, cytoInstrumentModel);
                    String cytoPlateWell = cytoResultSet.getString("plate_well");

                    cytoStatement = _connection.createStatement();
                    cytoQueryString = "SELECT event.id, event.fluorescence,event.side_scatter,event.forward_scatter" +
                            " FROM event INNER JOIN cytometer_measurement ON event.cytometer_measurement_id = cytometer_measurement.id" +
                            " WHERE well = '" + cytoPlateWell.toLowerCase() + "' AND cytometer_read_id = " + String.valueOf(cytoReadID);

                    ResultSet cytoMeasurementResultSet = cytoStatement.executeQuery(cytoQueryString);
                    CytometerMeasurement[] cytoMeasurements = this.createCytometerMeasurementArray(cytoMeasurementResultSet);
                    CytometerRead cytoRead = new CytometerRead(cytoReadID, cytoReadDate, cytoReadTypeCode, cytoReadTypeName, cytoInstrument, cytoMeasurements);
                    cytoReads.add(cytoRead);
                }

                if(reads.size() > 0)
                {
                    readArray = new Read[reads.size()];

                    if(cytoReads.size() > 0)
                    {
                        CytometerRead[] cytoReadArray = new CytometerRead[cytoReads.size()];
                        performance = new ConstructPerformance(reads.toArray(readArray), cytoReads.toArray(cytoReadArray));
                        construct.setPerformance(performance);
                        respond(_response, construct, _format);
                    }
                    else
                    {
                        performance = new ConstructPerformance(reads.toArray(readArray), null);
                        construct.setPerformance(performance);
                        respond(_response, construct, _format);
                    }
                }
                else
                {
                    if(cytoReads.size() > 0)
                    {
                        CytometerRead[] cytoReadArray = new CytometerRead[cytoReads.size()];
                        performance = new ConstructPerformance(null, cytoReads.toArray(cytoReadArray));
                        construct.setPerformance(performance);
                        respond(_response, construct, _format);
                    }
                    else
                    {
                        this.textError(_response, "Performance data is not available.");
                    }
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
            }
            finally
            {
                try
                {
                    _connection.close();
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
        }
        else
        {
            textError(_response, "The BIOFAB Data Access Web Service requires a construct ID to provide performance data. Please review the application interface documentation.");
        }
    }
    
//    protected String generateCSV(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("id,biofab_id,description,bulk_gene_expression,bulk_gene_expression_sd,gene_expression_per_cell,gene_expression_per_cell_sd\n");
//
//        while (resultSet.next())
//        {
//            String id = resultSet.getString("id");
//            String biofab_id = resultSet.getString("biofab_id");
//            String description = resultSet.getString("description");
//            String bulkGeneExpression = resultSet.getString("bulk_gene_expression");
//            String bulkGeneExpressionSD = resultSet.getString("bulk_gene_expression_sd");
//            String geneExpressionPerCell = resultSet.getString("gene_expression_per_cell");
//            String geneExpressionPerCellSD = resultSet.getString("gene_expression_per_cell_sd");
//
//            responseText.append(id);
//            responseText.append(",");
//            responseText.append(biofab_id);
//            responseText.append(",");
//            responseText.append(description);
//            responseText.append(",");
//            responseText.append(bulkGeneExpression);
//            responseText.append(",");
//            responseText.append(bulkGeneExpressionSD);
//            responseText.append(",");
//            responseText.append(geneExpressionPerCell);
//            responseText.append(",");
//            responseText.append(geneExpressionPerCellSD);
//            responseText.append("\n");
//        }
//
//        return responseText.toString();
//    }

//    protected String generateJSON(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("[\n");
//
//        while (resultSet.next())
//        {
//            String id = resultSet.getString("id");
//            String collectionID = resultSet.getString("collection_id");
//            String biofabID = resultSet.getString("biofab_id");
//            String description = resultSet.getString("description");
//            String bulkGeneExpression = resultSet.getString("bulk_gene_expression");
//            String bulkGeneExpressionSD = resultSet.getString("bulk_gene_expression_sd");
//            String geneExpressionPerCell = resultSet.getString("gene_expression_per_cell");
//            String geneExpressionPerCellSD = resultSet.getString("gene_expression_per_cell_sd");
//
//            responseText.append("{'id':");
//            responseText.append(id);
//            responseText.append(", ");
//            responseText.append("'collection_id':'");
//            responseText.append(collectionID);
//            responseText.append("', ");
//            responseText.append("'biofab_id':'");
//            responseText.append(biofabID);
//            responseText.append("', ");
//            responseText.append("'description':\"");
//            responseText.append(description);
//            responseText.append("\", ");
//            responseText.append("'bulk_gene_expression':");
//            responseText.append(bulkGeneExpression);
//            responseText.append(", ");
//            responseText.append("'bulk_gene_expression_sd':");
//            responseText.append(bulkGeneExpressionSD);
//            responseText.append(", ");
//            responseText.append("'gene_expression_per_cell':");
//            responseText.append(geneExpressionPerCell);
//            responseText.append(", ");
//            responseText.append("'gene_expression_per_cell_sd':");
//            responseText.append(geneExpressionPerCellSD);
//
//            if(resultSet.isLast() == false)
//            {
//                responseText.append("},");
//                responseText.append("\n");
//            }
//            else
//            {
//                responseText.append("}\n]");
//            }
//        }
//
//        return responseText.toString();
//    }

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

    protected Measurement[] createMeasurementArray(ResultSet resultSet) throws SQLException
    {
        Measurement             measurement = null;
        ArrayList<Measurement>  measurementArrayList = new ArrayList<Measurement>();
        Measurement[]           measurements = null;
        float                   value;
        int                     id;
        Time                    time;
        long                    minutes;
        long                    firstTime = 0;

        while (resultSet.next())
        {
            if(resultSet.isFirst())
            {
                time = resultSet.getTime("time");
                firstTime = time.getTime()/1000/60;

                id = resultSet.getInt("id");
                value = resultSet.getFloat("value");
                measurement = new Measurement(id, 0, value);
                measurementArrayList.add(measurement);
            }
            else
            {
                time = resultSet.getTime("time");
                minutes = (time.getTime()/1000/60) - firstTime;

                id = resultSet.getInt("id");
                value = resultSet.getFloat("value");
                measurement = new Measurement(id, minutes, value);
                measurementArrayList.add(measurement);
            }
        }

        measurements = new Measurement[measurementArrayList.size()];
        measurements = measurementArrayList.toArray(measurements);

        return measurements;
    }

    protected CytometerMeasurement[] createCytometerMeasurementArray(ResultSet resultSet) throws SQLException
    {
        CytometerMeasurement             measurement = null;
        ArrayList<CytometerMeasurement>  measurementArrayList = new ArrayList<CytometerMeasurement>();
        CytometerMeasurement[]           measurements = null;
        long                    id;
        float                   fluorescence;
        float                   forwardScatter;
        float                   sideScatter;

        while (resultSet.next())
        {
            id = resultSet.getInt("id");
            fluorescence = resultSet.getFloat("fluorescence");
            forwardScatter = resultSet.getFloat("forward_scatter");
            sideScatter = resultSet.getFloat("side_scatter");
            measurement = new CytometerMeasurement(id, fluorescence, forwardScatter, sideScatter);
            measurementArrayList.add(measurement);
        }

        measurements = new CytometerMeasurement[measurementArrayList.size()];
        measurements = measurementArrayList.toArray(measurements);

        return measurements;
    }

//    protected String generateCSV(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("time,value\n");
//
//        while (resultSet.next())
//        {
//            String time = resultSet.getString("time");
//            String value = resultSet.getString(2);
//
//            responseText.append(time);
//            responseText.append(",");
//            responseText.append(value);
//            responseText.append("\n");
//        }
//
//        return responseText.toString();
//    }

    protected void respond(HttpServletResponse response, Construct construct, String format)
    {
        String responseString;

        if(format.equalsIgnoreCase("json"))
        {
           responseString = generateJSON(construct);
           this.textSuccess(response, responseString);

        }
        else
        {
           responseString = generateJSON(construct);
           this.textSuccess(response, responseString);
        }
    }
}
