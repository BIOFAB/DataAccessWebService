package org.biofab.webservices.dataaccess;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import org.biofab.model.Terminator;


@WebServlet(name="TerminatorServlet", urlPatterns={"/terminators/*"})
public class TerminatorServlet extends DataAccessServlet
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
        Statement statement = null;
        //String collectionID = request.getParameter("collectionid");
        String format = request.getParameter("format");
        String responseString = null;
        String queryString;
        ArrayList<Terminator>   arrayList;
        Terminator[]            terminators = null;
        Terminator              terminator = null;
        int                     id;
        String                  biofabID = null;
        String                  description = null;
        float                   terminationEfficiency;
        float                   terminationEfficiencySD;
        String                  dnaSequence;
              
        try
        {
            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
            statement = _connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM terminator_performance_summary ORDER BY terminator_performance_summary.id ASC");
            arrayList = new ArrayList<Terminator>();

            while (resultSet.next())
            {
                id = resultSet.getInt("id");
                biofabID = resultSet.getString("biofab_id");
                description = resultSet.getString("description");
                terminationEfficiency = resultSet.getFloat("termination_efficiency");
                terminationEfficiencySD = resultSet.getFloat("termination_efficiency_sd");
                dnaSequence = resultSet.getString("dna_sequence");

                terminator = new Terminator(id, biofabID, description, terminationEfficiency, terminationEfficiencySD, dnaSequence);
                arrayList.add(terminator);
            }

            if(format.equalsIgnoreCase("json"))
            {
               responseString = generateJSON(arrayList.toArray());
               this.textSuccess(response, responseString);
            }
            else
            {
               responseString = generateJSON(arrayList.toArray());
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
                _connection.close();
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
        textError(response, "Post requests are not serviced by the Constructs web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        textError(response, "Put requests are not serviced by the Constructs web service");
    }

    // Utility Functions

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
}
