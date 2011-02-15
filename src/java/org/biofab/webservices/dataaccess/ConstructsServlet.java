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
                ResultSet resultSet = statement.executeQuery("SELECT * FROM new_pilot_project_construct ORDER BY new_pilot_project_construct.fluorescence_over_od_mean DESC");

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
        StringBuilder responseText = new StringBuilder("id,biofab_id,description,fluorescence_over_od_mean,fluorescence_over_od_sd,fluorescence_per_cell_mean,fluorescence_per_cell_sd\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("id");
            String biofab_id = resultSet.getString("biofab_id");
            String description = resultSet.getString("description");
            String fluorescenceODMean = resultSet.getString("fluorescence_over_od_mean");
            String fluorescenceODSD = resultSet.getString("fluorescence_over_od_sd");
            String fluorescenceCellMean = resultSet.getString("fluorescence_per_cell_mean");
            String fluorescenceCellSD = resultSet.getString("fluorescence_per_cell_sd");

            responseText.append(id);
            responseText.append(",");
            responseText.append(biofab_id);
            responseText.append(",");
            responseText.append(description);
            responseText.append(",");
            responseText.append(fluorescenceODMean);
            responseText.append(",");
            responseText.append(fluorescenceODSD);
            responseText.append(",");
            responseText.append(fluorescenceCellMean);
            responseText.append(",");
            responseText.append(fluorescenceCellSD);
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
            String fluorescenceODMean = resultSet.getString("fluorescence_over_od_mean");
            String fluorescenceODSD = resultSet.getString("fluorescence_over_od_sd");
            String fluorescenceCellMean = resultSet.getString("fluorescence_per_cell_mean");
            String fluorescenceCellSD = resultSet.getString("fluorescence_per_cell_sd");

            responseText.append("{'id':");
            responseText.append(id);
            responseText.append(", ");
            responseText.append("'biofab_id':'");
            responseText.append(biofab_id);
            responseText.append("', ");
            responseText.append("'description':\"");
            responseText.append(description);
            responseText.append("\", ");
            responseText.append("'fluorescence_over_od_mean':");
            responseText.append(fluorescenceODMean);
            responseText.append(", ");
            responseText.append("'fluorescence_over_od_sd':");
            responseText.append(fluorescenceODSD);
            responseText.append(", ");
            responseText.append("'fluorescence_per_cell_mean':");
            responseText.append(fluorescenceCellMean);
            responseText.append(", ");
            responseText.append("'fluorescence_per_cell_sd':");
            responseText.append(fluorescenceCellSD);

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
