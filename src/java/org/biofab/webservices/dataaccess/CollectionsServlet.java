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

        try
        {
            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
            statement = _connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT collection.biofab_id, collection.version, collection.description, collection.name, collection.id FROM collection ORDER BY collection.id ASC");

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
        textError(response, "Post requests are not serviced by the Collections web service");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        textError(response, "Put requests are not serviced by the Collections web service");
    }

    // Utility Functions

    protected String generateCSV(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("id,name,description,version\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("biofab_id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            String version = resultSet.getString("version");

            responseText.append(id);
            responseText.append(",");
            responseText.append(name);
            responseText.append(",");
            responseText.append(description);
            responseText.append(",");
            responseText.append(version);
            responseText.append("\n");
        }

        return responseText.toString();
    }

    protected String generateJSON(ResultSet resultSet) throws SQLException
    {
        StringBuilder responseText = new StringBuilder("{'collections':[\n");

        while (resultSet.next())
        {
            String id = resultSet.getString("biofab_id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            String version = resultSet.getString("version");

            responseText.append("{'id':'");
            responseText.append(id);
            responseText.append("', ");
            responseText.append("'name':\"");
            responseText.append(name);
            responseText.append("\", ");
            responseText.append("'description':'");
            responseText.append(description);
            responseText.append("', ");
            responseText.append("'version':'");
            responseText.append(version);

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
