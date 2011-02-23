package org.biofab.webservices.dataaccess;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name="AnnotatedPartsServlet", urlPatterns={"/annotatedparts/*"})
public class AnnotatedPartsServlet extends DataAccessServlet
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
        String queryString = null;

        //TODO Do stronger validation of collectionid

        if(collectionID != null && collectionID.length() > 0)
        {
            queryString = "SELECT annotated_part.biofab_id, feature.biofab_type, feature.description, feature.dna_sequence "
                    + "FROM collection_annotated_part "
                    + "INNER JOIN collection ON collection_annotated_part.collection_id = collection.id "
                    + "INNER JOIN annotated_part ON collection_annotated_part.part_id = annotated_part.id "
                    + "INNER JOIN feature ON annotated_part.feature_id = feature.id "
                    + "WHERE collection.id = " + collectionID + " "
                    + "ORDER BY feature.biofab_type DESC, annotated_part.id ASC";
        }
        else
        {
            queryString = "SELECT annotated_part.biofab_id, feature.biofab_type, feature.description, feature.dna_sequence "
                    + "FROM annotated_part "
                    + "INNER JOIN feature ON annotated_part.feature_id = feature.id "
                    + "ORDER BY feature.biofab_type DESC, annotated_part.id ASC;";
        }

        try
        {
            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
            statement = _connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);

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

    // Utility Functions

    protected String generateCSV(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("id,type,description,dna_sequence\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("biofab_id");
            String type = resultSet.getString("biofab_type");
            String description = resultSet.getString("description");
            String sequence = resultSet.getString("dna_sequence");

            responseText.append(id);
            responseText.append(",");
            responseText.append(type);
            responseText.append(",");
            responseText.append(description);
            responseText.append(",");
            responseText.append(sequence);
            responseText.append("\n");
        }

        return responseText.toString();
    }

    protected String generateJSON(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("{'annotatedparts':[\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("biofab_id");
            String type = resultSet.getString("biofab_type");
            String description = resultSet.getString("description");
            String sequence = resultSet.getString("dna_sequence");

            responseText.append("{'id':'");
            responseText.append(id);
            responseText.append("', ");
            responseText.append("'type':\"");
            responseText.append(type);
            responseText.append("\", ");
            responseText.append("'description':\"");
            responseText.append(description);
            responseText.append("\", ");
            responseText.append("'sequence':'");
            responseText.append(sequence);

            if(resultSet.isLast() == false)
            {
                responseText.append("'},");
                responseText.append("\n");
            }
            else
            {
                responseText.append("'}\n]}");
            }
        }

        return responseText.toString();
    }
}
