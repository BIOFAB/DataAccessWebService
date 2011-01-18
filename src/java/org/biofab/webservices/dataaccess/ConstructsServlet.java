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
        String projectID = request.getParameter("projectid");
        String format = request.getParameter("format");
        String responseString = null;

        if(projectID != null && projectID.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT pilot_project_construct.id,pilot_project_construct.description,pilot_project_construct.mid_log_phase_fluorescence,pilot_project_construct.chassis,pilot_project_construct.media FROM pilot_project_construct ORDER BY pilot_project_construct.mid_log_phase_fluorescence DESC");

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
            textError(response, "The BIOFAB Data Access Web Service requires a project ID to provide annotated parts. Please review the application interface documentation.");
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
        StringBuilder responseText = new StringBuilder("id,description,mid_log_phase_fluorescence,chassis,media\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("id");
            String description = resultSet.getString("description");
            String fluorescence = resultSet.getString("mid_log_phase_fluorescence");
            String chassis = resultSet.getString("chassis");
            String media = resultSet.getString("media");

            responseText.append(id);
            responseText.append(",");
            responseText.append(description);
            responseText.append(",");
            responseText.append(fluorescence);
            responseText.append(",");
            responseText.append(chassis);
            responseText.append(",");
            responseText.append(media);
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
            String description = resultSet.getString("description");
            String fluorescence = resultSet.getString("mid_log_phase_fluorescence");
            String chassis = resultSet.getString("chassis");
            String media = resultSet.getString("media");

            responseText.append("{'id':'");
            responseText.append(id);
            responseText.append("', ");
            responseText.append("'description':\"");
            responseText.append(description);
            responseText.append("\", ");
            responseText.append("'mid_log_phase_fluorescence':");
            responseText.append(fluorescence);
            responseText.append(", ");
            responseText.append("'chassis':'");
            responseText.append(chassis);
            responseText.append("', 'media':'");
            responseText.append(media);

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
