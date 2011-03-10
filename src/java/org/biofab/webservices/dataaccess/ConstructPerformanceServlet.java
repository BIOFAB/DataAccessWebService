/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.biofab.webservices.dataaccess;

import org.biofab.model.Construct;
import org.biofab.model.ConstructPerformance;
import org.biofab.model.Read;
import org.biofab.model.Instrument;
import org.biofab.model.Measurement;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


@WebServlet(name="ConstructPerformanceServlet", urlPatterns={"/construct/performance/*"})
public class ConstructPerformanceServlet extends DataAccessServlet
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
        String queryString = null;

        if(constructID != null && constructID.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();

                // TODO Do RegExp validation on constructID

                if(constructID != null && constructID.length() > 0)
                {
                    queryString = "SELECT * FROM construct_read_view WHERE construct_read_view.construct_biofab_id = '" + constructID + "' ORDER BY construct_read_view.read_id ASC";
                }
                else
                {
                    // TODO Deal with the null case
                }

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
        Gson                    gson = null;
        Construct               construct = null;
        ConstructPerformance    performance = null;
        ArrayList<Read>         reads;
        Read                    read = null;
        int                     constructID;
        String                  constructBiofabID = null;
        String                  responseString = null;
        int                     readID;
        String                  readDate;
        
        gson = new Gson();
        reads = new ArrayList<Read>();

        while (resultSet.next())
        {
            if(construct == null)
            {
                constructID = resultSet.getInt("construct_id");
                constructBiofabID = resultSet.getString("construct_biofab_id");
                construct= new Construct(constructID, constructBiofabID, null);
            }

            readID = resultSet.getInt("read_id");
            readDate = resultSet.getString("read_date");

            //read = new Read(String date, String typeCode, String typeName, Instrument instrument, Measurement[] measurements);
        }

        responseString = gson.toJson(construct);

        if(responseString == null || responseString.length() == 0)
        {
            //Throw exception
        }

        return responseString;
    }

//    protected String generateJSON(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("TEST");
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
}
