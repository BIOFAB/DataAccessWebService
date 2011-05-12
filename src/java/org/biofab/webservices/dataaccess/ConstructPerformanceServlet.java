/*
 * 
 * 
 */

package org.biofab.webservices.dataaccess;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biofab.model.Construct;
import org.biofab.model.ConstructPerformance;
import org.biofab.model.Read;
import org.biofab.model.CytometerRead;
import org.biofab.model.Instrument;
import org.biofab.model.Measurement;
import org.biofab.model.CytometerMeasurement;


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
        Statement               cytoStatement = null;
        String                  constructIDParam = request.getParameter("id");
        String                  format = request.getParameter("format");
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
                cytoStatement = _connection.createStatement();
                queryString = "SELECT * FROM construct_read_view WHERE UPPER(construct_read_view.construct_biofab_id) = '" + constructIDParam.toUpperCase() + "' ORDER BY construct_read_view.read_id ASC";
                String cytoQueryString = "SELECT * FROM construct_cytometer_read_view WHERE UPPER(construct_cytometer_read_view.construct_biofab_id) = '" + constructIDParam.toUpperCase() + "' ORDER BY construct_cytometer_read_view.read_id ASC";
                ResultSet resultSet = statement.executeQuery(queryString);
                ResultSet cytoResultSet = cytoStatement.executeQuery(cytoQueryString);
                reads = new ArrayList<Read>();
                ArrayList<CytometerRead>cytoReads = new ArrayList<CytometerRead>();

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
                    queryString = "SELECT measurement.id, measurement.time, measurement." + plateWell.toLowerCase() + " AS value FROM measurement WHERE measurement.read_id = " + String.valueOf(readID) + " ORDER BY measurement.time ASC";
                    ResultSet measurementResultSet = statement.executeQuery(queryString);
                    measurements = this.createMeasurementArray(measurementResultSet);
                    read = new Read(readID, readDate, readTypeCode, readTypeName, instrument, measurements);
                    reads.add(read);
                }

                while (cytoResultSet.next())
                {
                    if(construct == null)
                    {
                        int cytoConstructID = cytoResultSet.getInt("construct_id");
                        String cytoConstructBiofabID = cytoResultSet.getString("construct_biofab_id");
                        construct = new Construct(cytoConstructID, cytoConstructBiofabID, null);
                    }

                    int cytoReadID = cytoResultSet.getInt("read_id");
                    String cytoReadDate = cytoResultSet.getString("read_date");
                    String cytoReadTypeCode = cytoResultSet.getString("read_type_code");
                    String cytoReadTypeName = cytoResultSet.getString("read_type_name");
                    String cytoInstrumentType = cytoResultSet.getString("instrument_type");
                    String cytoInstrumentMake = cytoResultSet.getString("instrument_make");
                    String cytoInstrumentModel = cytoResultSet.getString("instrument_model");
                    Instrument cytoInstrument = new Instrument(cytoInstrumentType, cytoInstrumentMake, cytoInstrumentModel);
                    String cytoPlateWell = cytoResultSet.getString("plate_well");

                    cytoStatement = _connection.createStatement();
                    cytoQueryString = "SELECT event.id, event.fluorescence,event.side_scatter,event.forward_scatter" +
                            " FROM event INNER JOIN cytometer_measurement ON event.cytometer_measurement_id = cytometer_measurement.id" +
                            " WHERE well = '" + cytoPlateWell.toLowerCase() + "' AND cytometer_read_id = " + String.valueOf(cytoReadID);

                    ResultSet cytoMeasurementResultSet = cytoStatement.executeQuery(cytoQueryString);
                    CytometerMeasurement[] cytoMeasurements = this.createCytometerMeasurementArray(cytoMeasurementResultSet);
                    CytometerRead cytoRead = new CytometerRead(cytoReadID, cytoReadDate, cytoReadTypeCode, cytoReadTypeName, cytoInstrument, cytoMeasurements);
                    cytoReads.add(cytoRead);
                }

                if(reads.size() > 0)
                {
                    readArray = new Read[reads.size()];

                    if(cytoReads.size() > 0)
                    {
                        CytometerRead[] cytoReadArray = new CytometerRead[cytoReads.size()];
                        performance = new ConstructPerformance(reads.toArray(readArray), cytoReads.toArray(cytoReadArray));
                        construct.setPerformance(performance);
                        respond(response, construct, format);
                    }
                    else
                    {
                        performance = new ConstructPerformance(reads.toArray(readArray), null);
                        construct.setPerformance(performance);
                        respond(response, construct, format);
                    }
                }
                else
                {
                    if(cytoReads.size() > 0)
                    {
                        CytometerRead[] cytoReadArray = new CytometerRead[cytoReads.size()];
                        performance = new ConstructPerformance(null, cytoReads.toArray(cytoReadArray));
                        construct.setPerformance(performance);
                        respond(response, construct, format);
                    }
                    else
                    {
                        this.textError(response, "Performance data is not available.");
                    }
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
        float                   value;
        int                     id;
        Time                    time;
        long                    minutes;
        long                    firstTime = 0;

        while (resultSet.next())
        {
            if(resultSet.isFirst())
            {
                time = resultSet.getTime("time");
                firstTime = time.getTime()/1000/60;

                id = resultSet.getInt("id");
                value = resultSet.getFloat("value");
                measurement = new Measurement(id, 0, value);
                measurementArrayList.add(measurement);
            }
            else
            {
                time = resultSet.getTime("time");
                minutes = (time.getTime()/1000/60) - firstTime;

                id = resultSet.getInt("id");
                value = resultSet.getFloat("value");
                measurement = new Measurement(id, minutes, value);
                measurementArrayList.add(measurement);
            }
        }

        measurements = new Measurement[measurementArrayList.size()];
        measurements = measurementArrayList.toArray(measurements);

        return measurements;
    }

    protected CytometerMeasurement[] createCytometerMeasurementArray(ResultSet resultSet) throws SQLException
    {
        CytometerMeasurement             measurement = null;
        ArrayList<CytometerMeasurement>  measurementArrayList = new ArrayList<CytometerMeasurement>();
        CytometerMeasurement[]           measurements = null;
        long                    id;
        float                   fluorescence;
        float                   forwardScatter;
        float                   sideScatter;

        while (resultSet.next())
        {
            id = resultSet.getInt("id");
            fluorescence = resultSet.getFloat("fluorescence");
            forwardScatter = resultSet.getFloat("forward_scatter");
            sideScatter = resultSet.getFloat("side_scatter");
            measurement = new CytometerMeasurement(id, fluorescence, forwardScatter, sideScatter);
            measurementArrayList.add(measurement);
        }

        measurements = new CytometerMeasurement[measurementArrayList.size()];
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

    protected void respond(HttpServletResponse response, Construct construct, String format)
    {
        String responseString;

        if(format.equalsIgnoreCase("json"))
        {
           responseString = generateJSON(construct);
           this.textSuccess(response, responseString);

        }
        else
        {
           responseString = generateJSON(construct);
           this.textSuccess(response, responseString);
        }
    }

//    protected String generateJSON(Construct construct)
//    {
//        Gson    gson;
//        String  responseString;
//
//        gson = new Gson();
//        responseString = gson.toJson(construct);
//
//        if(responseString == null || responseString.length() == 0)
//        {
//            //Throw exception
//        }
//
//        return responseString;
//    }
}
