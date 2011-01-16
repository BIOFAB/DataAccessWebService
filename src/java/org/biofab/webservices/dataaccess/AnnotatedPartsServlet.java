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


@WebServlet(name="AnnotatedPartsServlet", urlPatterns={"/annotatedparts/*"})
public class AnnotatedPartsServlet extends DataAccessServlet
{
    Connection connection = null;

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
        String projectID = request.getParameter("projectid");
        String format = request.getParameter("format");
        String responseString = null;

        if(projectID != null && projectID.length() > 0)
        {
            try
            {
                connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT annotated_part.biofab_id, feature.biofab_type, feature.description, feature.seq FROM annotated_part, feature WHERE annotated_part.feature_id=feature.id ORDER BY feature.biofab_type DESC, annotated_part.id ASC;");

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
                    connection.close();
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
            textError(response, "The BIOFAB Data Access Web Service requires a project ID to provide annotated parts. Please review the application interface documentation.");
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
            String sequence = resultSet.getString("seq");

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
            String sequence = resultSet.getString("seq");

            responseText.append("{'id':'");
            responseText.append(id);
            responseText.append("', ");
            responseText.append("'type':\"");
            responseText.append(type);
            responseText.append("\", ");
            responseText.append("'description':'");
            responseText.append(description);
            responseText.append("', ");
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
