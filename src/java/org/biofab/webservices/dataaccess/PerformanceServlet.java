/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.webservices.dataaccess;


import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name="PerformanceServlet", urlPatterns={"/performance/*"})
public class PerformanceServlet extends DataAccessServlet
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
        String constructID = request.getParameter("constructid");
        String format = request.getParameter("format");
        String responseString = null;

        if(constructID != null && constructID.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT  plate_raw_measurement.\"time\", plate_raw_measurement.a1 FROM plate_raw_measurement WHERE plate_raw_measurement.plate_read_id = 1;");

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
            textError(response, "The BIOFAB Data Access Web Service requires a construct ID to provide performance data. Please review the application interface documentation.");
        }


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        jsonError(response, "Post requests are not serviced by the Performance web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        jsonError(response, "Put requests are not serviced by the Performance web service");
    }

    // Utility Functions

    protected String generateCSV(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("time,value\n");

        while (resultSet.next())
        {
            String time = resultSet.getString("time");
            String value = resultSet.getString(2);

            responseText.append(time);
            responseText.append(",");
            responseText.append(value);
            responseText.append("\n");
        }

        return responseText.toString();
    }

    protected String generateJSON(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("TEST");

        while (resultSet.next())
        {
//            String time = resultSet.getString("time");
//            String value = resultSet.getString(2);
//
//            responseText.append(time);
//            responseText.append(",");
//            responseText.append(value);
//            responseText.append("\n");
        }

        return responseText.toString();
    }
}
