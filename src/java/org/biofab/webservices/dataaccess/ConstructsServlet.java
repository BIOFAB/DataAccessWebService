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


@WebServlet(name="ConstructsServlet", urlPatterns={"/constructs/*"})
public class ConstructsServlet extends DataAccessServlet
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
        String collectionID = request.getParameter("collectionid");
        String format = request.getParameter("format");
        String responseString = null;

        if(collectionID != null && collectionID.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM new_pilot_project_construct ORDER BY new_pilot_project_construct.bulk_gene_expression DESC, new_pilot_project_construct.reporter DESC");

                if(format.equalsIgnoreCase("json"))
                {
                   responseString = generateJSON(resultSet);
                   this.textSuccess(response, responseString);
                }
                else
                {
                   responseString = generateCSV(resultSet);
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
        else
        {
            textError(response, "The BIOFAB Data Access Web Service requires a collection ID to provide construct information. Please review the application interface documentation.");
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

    protected String generateCSV(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("id,biofab_id,description,reporter,bulk_gene_expression,bulk_gene_expression_sd,gene_expression_per_cell,gene_expression_per_cell_sd\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("id");
            String biofab_id = resultSet.getString("biofab_id");
            String description = resultSet.getString("description");
            String reporter = resultSet.getString("reporter");
            String bulkGeneExpression = resultSet.getString("bulk_gene_expression");
            String bulkGeneExpressionSD = resultSet.getString("bulk_gene_expression_sd");
            String geneExpressionPerCell = resultSet.getString("gene_expression_per_cell");
            String geneExpressionPerCellSD = resultSet.getString("gene_expression_per_cell_sd");

            responseText.append(id);
            responseText.append(",");
            responseText.append(biofab_id);
            responseText.append(",");
            responseText.append(description);
            responseText.append(",");
            responseText.append(reporter);
            responseText.append(",");
            responseText.append(bulkGeneExpression);
            responseText.append(",");
            responseText.append(bulkGeneExpressionSD);
            responseText.append(",");
            responseText.append(geneExpressionPerCell);
            responseText.append(",");
            responseText.append(geneExpressionPerCellSD);
            responseText.append("\n");
        }

        return responseText.toString();
    }

    protected String generateJSON(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("{'constructs':[\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("id");
            String biofab_id = resultSet.getString("biofab_id");
            String description = resultSet.getString("description");
            String reporter = resultSet.getString("reporter");
            String bulkGeneExpression = resultSet.getString("bulk_gene_expression");
            String bulkGeneExpressionSD = resultSet.getString("bulk_gene_expression_sd");
            String geneExpressionPerCell = resultSet.getString("gene_expression_per_cell");
            String geneExpressionPerCellSD = resultSet.getString("gene_expression_per_cell_sd");

            responseText.append("{'id':");
            responseText.append(id);
            responseText.append(", ");
            responseText.append("'biofab_id':'");
            responseText.append(biofab_id);
            responseText.append("', ");
            responseText.append("'description':'");
            responseText.append(description);
            responseText.append("', ");
            responseText.append("'reporter':'");
            responseText.append(reporter);
            responseText.append("', ");
            responseText.append("'bulk_gene_expression':");
            responseText.append(bulkGeneExpression);
            responseText.append(", ");
            responseText.append("'bulk_gene_expression_sd':");
            responseText.append(bulkGeneExpressionSD);
            responseText.append(", ");
            responseText.append("'gene_expression_per_cell':");
            responseText.append(geneExpressionPerCell);
            responseText.append(", ");
            responseText.append("'gene_expression_per_cell_sd':");
            responseText.append(geneExpressionPerCellSD);

            if(resultSet.isLast() == false)
            {
                responseText.append("},");
                responseText.append("\n");
            }
            else
            {
                responseText.append("}\n]}");
            }
        }

        return responseText.toString();
    }
}
