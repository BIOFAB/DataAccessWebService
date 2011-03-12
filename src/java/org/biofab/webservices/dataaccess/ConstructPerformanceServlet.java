/*
 * 
 * 
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Statement               statement = null;
        //Statement               secondStatement = null;
        String                  constructIDParam = request.getParameter("id");
        String                  format = request.getParameter("format");
        String                  responseString = null;
        String                  queryString = null;
        Construct               construct = null;
        ConstructPerformance    performance = null;
        ArrayList<Read>         reads;
        Read[]                  readArray = null;
        Read                    read = null;
        int                     constructID;
        String                  constructBiofabID = null;
        int                     readID;
        String                  readDate = null;
        String                  readTypeCode = null;
        String                  readTypeName = null;
        String                  instrumentType = null;
        String                  instrumentMake = null;
        String                  instrumentModel = null;
        Instrument              instrument = null;
        Measurement[]           measurements = null;
        String                  plateWell = null;

        // TODO Do RegExp validation on constructIDParam
        if(constructIDParam != null && constructIDParam.length() > 0)
        {
            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                queryString = "SELECT * FROM construct_read_view WHERE UPPER(construct_read_view.construct_biofab_id) = '" + constructIDParam.toUpperCase() + "' ORDER BY construct_read_view.read_id ASC";
                ResultSet resultSet = statement.executeQuery(queryString);
                reads = new ArrayList<Read>();

                while (resultSet.next())
                {
                    if(construct == null)
                    {
                        constructID = resultSet.getInt("construct_id");
                        constructBiofabID = resultSet.getString("construct_biofab_id");
                        construct = new Construct(constructID, constructBiofabID, null);
                    }

                    readID = resultSet.getInt("read_id");
                    readDate = resultSet.getString("read_date");
                    readTypeCode = resultSet.getString("read_type_code");
                    readTypeName = resultSet.getString("read_type_name");
                    instrumentType = resultSet.getString("instrument_type");
                    instrumentMake = resultSet.getString("instrument_make");
                    instrumentModel = resultSet.getString("instrument_model");
                    instrument = new Instrument(instrumentType, instrumentMake, instrumentModel);
                    plateWell = resultSet.getString("plate_well");

                    statement = _connection.createStatement();
                    queryString = "SELECT measurement.time, measurement." + plateWell.toLowerCase() + " AS value FROM measurement WHERE measurement.read_id = " + String.valueOf(readID);
                    ResultSet measurementResultSet = statement.executeQuery(queryString);
                    measurements = this.createMeasurementArray(measurementResultSet);
                    read = new Read(readID, readDate, readTypeCode, readTypeName, instrument, measurements);
                    reads.add(read);
                }

                readArray = new Read[reads.size()];
                performance = new ConstructPerformance(reads.toArray(readArray));
                construct.setPerformance(performance);

                if(format.equalsIgnoreCase("json"))
                {
                   responseString = generateJSON(construct);
                   this.textSuccess(response, responseString);
                }
                else
                {
//                   responseString = generateCSV(resultSet);
//                   this.textSuccess(response, responseString);
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

    protected Measurement[] createMeasurementArray(ResultSet resultSet) throws SQLException
    {
        Measurement             measurement = null;
        ArrayList<Measurement>  measurementArrayList = new ArrayList<Measurement>();
        Measurement[]           measurements = null;
        String                  time;
        float                   value;

        while (resultSet.next())
        {
            time = resultSet.getString("time");
            value = resultSet.getFloat("value");
            measurement = new Measurement(time, value);
            measurementArrayList.add(measurement);
        }

        measurements = new Measurement[measurementArrayList.size()];
        measurements = measurementArrayList.toArray(measurements);

        return measurements;
    }

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

    protected String generateJSON(Construct construct)
    {
        Gson    gson;
        String  responseString;
        
        gson = new Gson();
        responseString = gson.toJson(construct);

        if(responseString == null || responseString.length() == 0)
        {
            //Throw exception
        }

        return responseString;
    }
}
