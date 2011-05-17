package org.biofab.webservices.dataaccess;

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

import org.biofab.model.Part;


@WebServlet(name="PartsServlet", urlPatterns={"/parts/*"})
public class PartsServlet extends DataAccessServlet
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
        String          collectionID = request.getParameter("collectionid");
        String          format = request.getParameter("format");
        String          responseString = null;
        String          queryString = null;
        int             collectionIDFromDB;
        int             id;
        String          biofabID = null;
        String          type = null;
        String          description = null;
        String          sequence = null;
        ArrayList<Part> parts = null;
        Part            part = null;


        //TODO Do stronger validation of collectionid

        if(collectionID != null && collectionID.length() > 0)
        {
            queryString = "SELECT *"
                    + "FROM annotated_part_view "
                    + "WHERE annotated_part_view.collection_id = " + collectionID
                    + "ORDER BY biofab_type DESC, id ASC;";
        }
        else
        {
            queryString = "SELECT *"
                    + "FROM annotated_part_view "
                    + "ORDER BY biofab_type DESC, id ASC;";
        }

        try
        {
            _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
            statement = _connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            parts = new ArrayList<Part>();
            
            while (resultSet.next())
            {
                collectionIDFromDB = resultSet.getInt("collection_id");
                id = resultSet.getInt("id");
                biofabID = resultSet.getString("biofab_id");
                type = resultSet.getString("biofab_type");
                description = resultSet.getString("description");
                sequence = resultSet.getString("dna_sequence");

                part = new Part(collectionIDFromDB, id, biofabID, description, type, sequence);
                parts.add(part);
            }

            if(format.equalsIgnoreCase("json"))
            {
               responseString = generateJSON(parts.toArray());
               this.textSuccess(response, responseString);
            }
            else
            {
               responseString = generateJSON(parts.toArray());
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

//    protected String generateCSV(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("id,type,description,dna_sequence\n");
//
//        while (resultSet.next())
//        {
//            String id = resultSet.getString("biofab_id");
//            String type = resultSet.getString("biofab_type");
//            String description = resultSet.getString("description");
//            String sequence = resultSet.getString("dna_sequence");
//
//            responseText.append(id);
//            responseText.append(",");
//            responseText.append(type);
//            responseText.append(",");
//            responseText.append(description);
//            responseText.append(",");
//            responseText.append(sequence);
//            responseText.append("\n");
//        }
//
//        return responseText.toString();
//    }

//    protected String generateJSON(ResultSet resultSet) throws SQLException
//    {
//        StringBuilder responseText = new StringBuilder("{'annotatedparts':[\n");
//
//        while (resultSet.next())
//        {
//            String id = resultSet.getString("biofab_id");
//            String type = resultSet.getString("biofab_type");
//            String description = resultSet.getString("description");
//            String sequence = resultSet.getString("dna_sequence");
//
//            responseText.append("{'id':'");
//            responseText.append(id);
//            responseText.append("', ");
//            responseText.append("'type':\"");
//            responseText.append(type);
//            responseText.append("\", ");
//            responseText.append("'description':\"");
//            responseText.append(description);
//            responseText.append("\", ");
//            responseText.append("'sequence':'");
//            responseText.append(sequence);
//
//            if(resultSet.isLast() == false)
//            {
//                responseText.append("'},");
//                responseText.append("\n");
//            }
//            else
//            {
//                responseText.append("'}\n]}");
//            }
//        }
//
//        return responseText.toString();
//    }
}
