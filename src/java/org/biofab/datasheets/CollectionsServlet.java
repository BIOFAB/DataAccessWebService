package org.biofab.datasheets;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biofab.datasheets.model.Collection;


@WebServlet(name="CollectionsServlet", urlPatterns={"/collections/*"})
public class CollectionsServlet extends DataAccessServlet
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
        String format = request.getParameter("format");
        String responseString = null;
        ArrayList<Collection> arrayList;
        Collection[] collections;
        Collection collection;
        int id;
        String biofabID;
        String name;
        String description;
        String version;
        String release_status;
        Date release_date;
        String chassis;

        try
        {
            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
            statement = _connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM collection ORDER BY collection.release_date DESC, collection.name ASC");
            arrayList = new ArrayList<Collection>();

            while (resultSet.next())
            {
                id = resultSet.getInt("id");
                biofabID = resultSet.getString("biofab_id");
                name = resultSet.getString("name");
                description = resultSet.getString("description");
                version = resultSet.getString("version");
                release_status = resultSet.getString("release_status");
                release_date = resultSet.getDate("release_date");
                chassis = resultSet.getString("chassis");

                collection = new Collection(id, biofabID, chassis, name, version, release_status, release_date, description);
                arrayList.add(collection);
            }

            if(format != null && format.length() > 0 && format.equalsIgnoreCase("json"))
            {
                responseString = this.generateJSON(arrayList.toArray());
                this.textSuccess(response, responseString);
            }
            else
            {
                responseString = this.generateJSON(arrayList.toArray());
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
        textError(response, "Post requests are not serviced by the Collections web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        textError(response, "Put requests are not serviced by the Collections web service");
    }

    // Utility Functions

//    protected String generateCSV(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("id,name,description,version\n");
//
//        while (resultSet.next())
//        {
//            String id = resultSet.getString("biofab_id");
//            String name = resultSet.getString("name");
//            String description = resultSet.getString("description");
//            String version = resultSet.getString("version");
//
//            responseText.append(id);
//            responseText.append(",");
//            responseText.append(name);
//            responseText.append(",");
//            responseText.append(description);
//            responseText.append(",");
//            responseText.append(version);
//            responseText.append("\n");
//        }
//
//        return responseText.toString();
//    }

}
