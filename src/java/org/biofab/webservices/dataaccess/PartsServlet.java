package org.biofab.webservices.dataaccess;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biofab.model.Part;
import org.sbolstandard.experiment.DnaComponent;
import org.sbolstandard.experiment.DnaSequence;
import org.sbolstandard.experiment.Measurement;


@WebServlet(name="PartsServlet", urlPatterns={"/parts/*"})
public class PartsServlet extends DataAccessServlet
{   
    String                  _collectionId;
    String                  _format;
    HttpServletRequest      _request;
    HttpServletResponse     _response;
    
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
        String                  responseString = null;
        String                  designQuery = null;
        String                  performanceQuery = null;
        String                  terminatorQuery = null;
        ResultSet               designResultSet = null;
        ResultSet               performanceResultSet = null;
        ResultSet               terminatorResultSet = null;
        ArrayList<DnaComponent> dnaComponents = null;
        
        _collectionId = request.getParameter("collectionid");
        _format = request.getParameter("format");
        _request = request;
        _response = response;

        if(_format == null || _format.length() == 0)
        {
            _format = "csv";
        }
        
        //TODO stronger validation of collectionid

        if(_collectionId != null)
        {
            if(_collectionId.equalsIgnoreCase("1") || _collectionId.equalsIgnoreCase("2") || _collectionId.equalsIgnoreCase("3"))
            {
                designQuery = "SELECT *"
                        + "FROM public.promoter_design_view "
                        + "WHERE collection_id = " + _collectionId;
                performanceQuery = "SELECT *"
                        + "FROM public.promoter_performance_view "
                        + "WHERE collection_id = " + _collectionId;
                
                designResultSet = fetchResultSet(designQuery, _format, _response);
                performanceResultSet = fetchResultSet(performanceQuery, _format, _response);
                dnaComponents = new ArrayList<DnaComponent>();
                dnaComponents = RetrievePromoters(designResultSet, performanceResultSet, dnaComponents);
            }
            else
            {
            
                if(_collectionId.equalsIgnoreCase("4"))
                {
                    terminatorQuery = "SELECT * FROM public.terminator_performance_view";
                    terminatorResultSet = fetchResultSet(terminatorQuery, _format, _response);
                    dnaComponents = new ArrayList<DnaComponent>();
                    dnaComponents = RetrieveTerminators(terminatorResultSet, dnaComponents);
                }
                else
                {
                    designQuery = "SELECT * FROM public.promoter_design_view";
                    performanceQuery = "SELECT * FROM public.promoter_performance_view";
                    terminatorQuery = "SELECT * FROM public.terminator_performance_view";
                    designResultSet = fetchResultSet(designQuery, _format, _response);
                    performanceResultSet = fetchResultSet(performanceQuery, _format, _response);
                    terminatorResultSet = fetchResultSet(terminatorQuery, _format, _response);
                    dnaComponents = new ArrayList<DnaComponent>();
                    dnaComponents = RetrievePromoters(designResultSet, performanceResultSet, dnaComponents);
                    dnaComponents = RetrieveTerminators(terminatorResultSet, dnaComponents);
                }
            }
        }
        else
        {
            designQuery = "SELECT * FROM public.promoter_design_view";
            performanceQuery = "SELECT * FROM public.promoter_performance_view";
            terminatorQuery = "SELECT * FROM public.terminator_performance_view";
            designResultSet = fetchResultSet(designQuery, _format, _response);
            performanceResultSet = fetchResultSet(performanceQuery, _format, _response);
            terminatorResultSet = fetchResultSet(terminatorQuery, _format, _response);
            dnaComponents = new ArrayList<DnaComponent>();
            dnaComponents = RetrievePromoters(designResultSet, performanceResultSet, dnaComponents);
            dnaComponents = RetrieveTerminators(terminatorResultSet, dnaComponents);
        }
    
        if(_format.equalsIgnoreCase("json"))
        {
           responseString = generateJSON(dnaComponents.toArray());
           this.textSuccess(response, responseString);
        }
        else
        {
            responseString = generateCSV(dnaComponents);
            this.textSuccess(response, responseString);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        textError(response, "Post requests are not serviced by the Annotated Parts web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        textError(response, "Put requests are not serviced by the Annotated Parts web service");
    }

//    protected DnaComponent createDnaComponent(ResultSet resultSet)
//    {
//        int collectionIDFromDB;
//        int id;
//        String biofabID = null;
//        String type = null;
//        String description = null;
//        String sequence = null;
//        DnaSequence dnaSequence = null;
//        DnaComponent dnaComponent = null;
//
//        try
//        {
//            collectionIDFromDB = resultSet.getInt("collection_id");
//            biofabID = resultSet.getString("biofab_id");
//            type = resultSet.getString("biofab_type");
//            description = resultSet.getString("description");
//            sequence = resultSet.getString("dna_sequence");
//            dnaSequence = new DnaSequence(sequence);
//            dnaComponent = new DnaComponent(collectionIDFromDB, biofabID, "", description, type, "part", false, dnaSequence);
//        }
//        catch (SQLException ex)
//        {
//            Logger.getLogger(PartsServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return dnaComponent;
//    }
    
    protected ArrayList<DnaComponent> RetrievePromoters(ResultSet designResultSet, ResultSet performanceResultSet, ArrayList<DnaComponent> dnaComponents)
    {
        DnaComponent            dnaComponent = null;
        DnaComponent            selectedDnaComponent = null;
        String                  biofabId;
        int                     collectionIdFromDb;
        int                     id;
        String                  type = null;
        String                  description = null;
        String                  sequence = null;
        DnaSequence             dnaSequence = null;
        Measurement             measurement = null;
        float                   bulkGeneExpression;
        float                   bulkGeneExpressionSD;
        float                   geneExpressionPerCell;
        float                   geneExpressionPerCellSD;
        String                  constructId;
        
        try
        {
            while (designResultSet.next())
            {
                biofabId = designResultSet.getString("part_biofab_id");
                collectionIdFromDb = designResultSet.getInt("collection_id");
                type = designResultSet.getString("type");
                description = designResultSet.getString("description");
                sequence = designResultSet.getString("dna_sequence");
                dnaSequence = new DnaSequence(sequence);
                dnaComponent = new DnaComponent(collectionIdFromDb, biofabId, "", description, type, false, dnaSequence);
                dnaComponents.add(dnaComponent);
                selectedDnaComponent = dnaComponent;
            }
            
            // TODO Refactor! Convert dnaComponents to a hash table.

            while (performanceResultSet.next())
            {
                biofabId = performanceResultSet.getString("part_biofab_id");

                for(DnaComponent component:dnaComponents)
                {
                    if(biofabId.equalsIgnoreCase(component.getDisplayID()))
                    {
                        constructId = performanceResultSet.getString("construct_id");
                        bulkGeneExpression = performanceResultSet.getFloat("bulk_gene_expression");
                        bulkGeneExpressionSD = performanceResultSet.getFloat("bulk_gene_expression_sd");
                        geneExpressionPerCell = performanceResultSet.getFloat("gene_expression_per_cell");
                        geneExpressionPerCellSD = performanceResultSet.getFloat("gene_expression_per_cell_sd");
                        
                        measurement = new Measurement("BGE", "Bulk Gene Expression", "AU/OD", "Pending", bulkGeneExpression, bulkGeneExpressionSD, constructId);
                        component.getPerformance().getMeasurements().add(measurement);
                        measurement = new Measurement("GEC", "Gene Expression per Cell", "AU", "Pending", geneExpressionPerCell, geneExpressionPerCellSD, constructId);
                        component.getPerformance().getMeasurements().add(measurement);

                        break;
                    }
                }
            }
        }
        catch (SQLException ex)
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
        finally
        {
            try
            {
                if(_connection != null)
                {
                    _connection.close();
                }
            }
            catch (SQLException ex)
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
        
        return dnaComponents;
    }
    
    protected ArrayList<DnaComponent> RetrieveTerminators(ResultSet terminatorResultSet, ArrayList<DnaComponent> dnaComponents)
    {
        DnaComponent            dnaComponent = null;
        DnaComponent            selectedDnaComponent = null;
        String                  biofabId;
        int                     collectionIdFromDb;
        int                     id;
        String                  type = null;
        String                  description = null;
        String                  sequence = null;
        DnaSequence             dnaSequence = null;
        Measurement             measurement = null;
        float                   terminationEfficiency;
        float                   terminationEfficiencySd;
        String                  constructBiofabId;
             
        try
        {
            while (terminatorResultSet.next())
            {
                biofabId = terminatorResultSet.getString("part_biofab_id");
                collectionIdFromDb = terminatorResultSet.getInt("collection_id");
                type = terminatorResultSet.getString("part_type");
                description = terminatorResultSet.getString("description");
                sequence = terminatorResultSet.getString("part_dna_sequence");
                dnaSequence = new DnaSequence(sequence);
                
                dnaComponent = new DnaComponent(collectionIdFromDb, biofabId, "", description, type, false, dnaSequence);
                
                constructBiofabId = terminatorResultSet.getString("construct_biofab_id");
                terminationEfficiency = terminatorResultSet.getFloat("termination_efficiency");
                terminationEfficiencySd = terminatorResultSet.getFloat("termination_efficiency_sd");
                measurement = new Measurement("TE", "Termination Efficiency", "%", "Pending", terminationEfficiency, terminationEfficiencySd, constructBiofabId);
                dnaComponent.getPerformance().getMeasurements().add(measurement);
                
                dnaComponents.add(dnaComponent);
            }
        }
        catch (SQLException ex)
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
        finally
        {
            try
            {
                if(_connection != null)
                {
                    _connection.close();
                }
            }
            catch (SQLException ex)
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
        
        return dnaComponents;
    }
    
    protected DnaComponent retrievePerformance(DnaComponent dnaComponent)
    {
        return null;
    }

    // Utility Functions

    protected String generateCSV(ArrayList<DnaComponent> dnaComponents)
    {
        ArrayList<String>       headers = new ArrayList<String>();
        ArrayList<String>       rows = new ArrayList<String>();
        ArrayList<String>       row;
        StringBuilder           responseText = new StringBuilder();
        String                  id;
        String                  type;
        String                  description;
        String                  sequence;
        int                     position;
        ArrayList<Measurement>  measurements;
        String                  label;
        boolean                 hasLabel;
        String                  csvLine;
        int                     gecCount;
        String                  measurementValue;
        String                  measurementSD;

        headers.add("id");
        headers.add("type");
        headers.add("description");
        headers.add("dna_sequence");
        headers.add("gene_expression_per_cell_(AU)");
        headers.add("gene_expression_per_cell_standard_deviation");

        for(DnaComponent dnaComponent:dnaComponents)
        {
            row = new ArrayList<String>();
            id = dnaComponent.getDisplayID();
            type = dnaComponent.getType();
            description = dnaComponent.getDescription();
            sequence = dnaComponent.getDnaSequence().getNucleotides();
            
            row.add(id);
            row.add(type);
            row.add(description);
            row.add(sequence);

            measurements = dnaComponent.getPerformance().getMeasurements();
            gecCount = 0;
            measurementValue = null;
            measurementSD = null;

            for(Measurement measurement:measurements)
            {
                if(measurement.getType().equalsIgnoreCase("GEC"))
                {
                    measurementValue = String.valueOf(measurement.getValue());
                    measurementSD = String.valueOf(measurement.getStandardDeviation());
                    gecCount++;
                }

//              for(headers.contains(measurement.getLabel()))
//              {
//                 if(header.equalsIgnoreCase(label));
//              }
            }

            if(gecCount == 1)
            {
                row.add(measurementValue);
                row.add(measurementSD);
            }
            
            csvLine = this.generateCsvLine(row);
            rows.add(csvLine);
        }

        csvLine = generateCsvLine(headers);
        responseText.append(csvLine);

        for(String rowString:rows)
        {
           responseText.append(rowString);
        }

        return responseText.toString();
    }

    protected String generateCsvLine(ArrayList<String>strings)
    {
        StringBuilder csvLine = new StringBuilder();

        for(String string:strings)
        {
           csvLine.append(string);
           csvLine.append(",");
        }

        csvLine.deleteCharAt(csvLine.length() - 1);
        csvLine.append("\n");

        return csvLine.toString();
    }

    protected Measurement retrieveMeasurement()
    {
        return null;
//            var measurement;
//            var performance = part.performance;
//            var measurements;
//            var measurementsCount;
//            var bgeMeasurements = [];
//            var maxMeasurement;
//
//            if(performance != undefined)
//            {
//                measurements = performance.measurements;
//                measurementsCount = measurements.length;
//
//                for(var i = 0; i < measurementsCount; i += 1)
//                {
//                    if(measurements[i].type === 'GEC')
//                    {
//                        //measurement = measurements[i];
//                        bgeMeasurements.push(measurements[i]);
//                    }
//                }
//
//                if(bgeMeasurements.length > 1)
//                {
//                    bgeMeasurements.sort(
//                        function(a,b)
//                        {
//                            return a.value - b.value;
//                        }
//                    );
//
//                    maxMeasurement = bgeMeasurements.pop();
//                    measurement = {label: 'Maximum ' + maxMeasurement.label, unit: maxMeasurement.unit, value: maxMeasurement.value};
//                }
//                else
//                {
//                    if(bgeMeasurements.length === 1)
//                    {
//                        measurement = bgeMeasurements[0];
//                    }
//                    else
//                    {
//                        measurement = {label: 'Unavailable', unit: 'None', value: 0};
//                    }
//                }
//            }
//            else
//            {
//                measurement = {label: 'Unavailable', unit: 'None', value: 0};
//            }
//
//            return measurement;
    }

}
