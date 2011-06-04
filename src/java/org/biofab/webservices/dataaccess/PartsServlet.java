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
    @Override
    public void init()
    {

    }

    @Override 
    public void destroy()
    {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        Statement               designStatement = null;
        Statement               performanceStatement = null;
        String                  collectionID = request.getParameter("collectionid");
        String                  format = request.getParameter("format");
        String                  responseString = null;
        String                  designQuery = null;
        String                  performanceQuery = null;

        //TODO Turn dnaComponents into a hash table
        ArrayList<DnaComponent> dnaComponents = null;
        DnaComponent            dnaComponent = null;
        DnaComponent            selectedDnaComponent = null;
        String                  biofabID;
        int                     collectionIDFromDB;
        int                     id;
        String                  type = null;
        String                  description = null;
        String                  sequence = null;
        DnaSequence             dnaSequence = null;
        ResultSet               designResultSet = null;
        ResultSet               performanceResultSet = null;
        Measurement             measurement = null;
        float                   bulkGeneExpression;
        float                   bulkGeneExpressionSD;
        float                   geneExpressionPerCell;
        float                   geneExpressionPerCellSD;

        //TODO Do stronger validation of collectionid

        if(collectionID != null && collectionID.length() > 0)
        {
            designQuery = "SELECT *"
                    + "FROM part_design_view "
                    + "WHERE collection_id = " + collectionID
                    + "ORDER BY collection_id ASC, part_id ASC;";
            performanceQuery = "SELECT *"
                    + "FROM part_performance_view "
                    + "WHERE collection_id = " + collectionID
                    + "ORDER BY collection_id ASC, part_id ASC;";
        }
        else
        {
            designQuery = "SELECT *"
                    + "FROM part_design_view "
                    + "ORDER BY collection_id ASC, part_id ASC;";
            performanceQuery = "SELECT *"
                    + "FROM part_performance_view "
                    + "ORDER BY collection_id ASC, part_id ASC;";
        }

        try
        {
            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
            designStatement = _connection.createStatement();
            designResultSet = designStatement.executeQuery(designQuery);
            dnaComponents = new ArrayList<DnaComponent>();

            while (designResultSet.next())
            {
                biofabID = designResultSet.getString("part_biofab_id");
                collectionIDFromDB = designResultSet.getInt("collection_id");
                type = designResultSet.getString("type");
                description = designResultSet.getString("description");
                sequence = designResultSet.getString("dna_sequence");
                dnaSequence = new DnaSequence(sequence);
                dnaComponent = new DnaComponent(collectionIDFromDB, biofabID, "", description, type, "part", false, dnaSequence);
                dnaComponents.add(dnaComponent);
                selectedDnaComponent = dnaComponent;
            }

            performanceStatement = _connection.createStatement();
            performanceResultSet = performanceStatement.executeQuery(performanceQuery);
            
            // TODO Refactor! Convert dnaComponents to a hash table.

            while (performanceResultSet.next())
            {
                biofabID = performanceResultSet.getString("part_biofab_id");

                for(DnaComponent component:dnaComponents)
                {
                    if(biofabID.equalsIgnoreCase(component.getDisplayID()))
                    {
                        bulkGeneExpression = performanceResultSet.getFloat("bulk_gene_expression");
                        bulkGeneExpressionSD = performanceResultSet.getFloat("bulk_gene_expression_sd");
                        geneExpressionPerCell = performanceResultSet.getFloat("gene_expression_per_cell");
                        geneExpressionPerCellSD = performanceResultSet.getFloat("gene_expression_per_cell_sd");
                        
                        measurement = new Measurement("BGE", "Bulk Gene Expression", "AU/OD", "Pending", bulkGeneExpression, bulkGeneExpressionSD);
                        component.getPerformance().getMeasurements().add(measurement);
                        measurement = new Measurement("GEC", "Gene Expression per Cell", "AU", "Pending", geneExpressionPerCell, geneExpressionPerCellSD);
                        component.getPerformance().getMeasurements().add(measurement);

                        break;
                    }
                }
            }

            if(format.equalsIgnoreCase("json"))
            {
               responseString = generateJSON(dnaComponents.toArray());
               this.textSuccess(response, responseString);
            }
            else
            {
               responseString = generateJSON(dnaComponents.toArray());
               this.textSuccess(response, responseString);
            }

        }
        catch (SQLException ex)
        {
            if(format != null && format.length() > 0)
            {
                if(format.equalsIgnoreCase("json"))
                {
                    jsonError(response, "Error while fetching data: " + ex.getMessage());

                }
                else
                {
                    textError(response, "Error while fetching data: " + ex.getMessage());
                }
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
                if(format != null && format.length() > 0)
                {
                    if(format.equalsIgnoreCase("json"))
                    {
                        jsonError(response, "Error while fetching data: " + ex.getMessage());
                    }
                    else
                    {
                        textError(response, "Error while fetching data: " + ex.getMessage());
                    }
                }
                else
                {
                    textError(response, "Error while fetching data: " + ex.getMessage());
                }
            }
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
    
    protected DnaComponent retrievePerformance(DnaComponent dnaComponent)
    {
        return null;
    }

    // Utility Functions

//    protected String generateCSV(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("id,type,description,dna_sequence\n");
//
//        while (resultSet.next())
//        {
//            String id = resultSet.getString("biofab_id");
//            String type = resultSet.getString("biofab_type");
//            String description = resultSet.getString("description");
//            String sequence = resultSet.getString("dna_sequence");
//
//            responseText.append(id);
//            responseText.append(",");
//            responseText.append(type);
//            responseText.append(",");
//            responseText.append(description);
//            responseText.append(",");
//            responseText.append(sequence);
//            responseText.append("\n");
//        }
//
//        return responseText.toString();
//    }
}
