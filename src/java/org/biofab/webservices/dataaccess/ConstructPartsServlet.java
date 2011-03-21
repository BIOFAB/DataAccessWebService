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
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import org.biofab.model.ConstructPartRelationship;


@WebServlet(name="ConstructPartsServlet", urlPatterns={"/constructparts/*"})
public class ConstructPartsServlet extends DataAccessServlet
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
        Statement                               statement = null;
        String                                  collectionID = request.getParameter("collectionid");
        String                                  format = request.getParameter("format");
        String                                  responseString = null;
        String                                  queryString = null;
        ArrayList<ConstructPartRelationship>    relationshipsArrayList;
        ConstructPartRelationship[]             relationships;
        ConstructPartRelationship               relationship;
        String                                  constructID = null;
        String                                  partID = null;

        //TODO Do stronger validation of collectionid
        //if(collectionID != null && collectionID.length() > 0)

        if(true)
        {
            queryString = "SELECT construct_part_view.construct_id, construct_part_view.part_id FROM construct_part_view";

            try
            {
                _connection = DriverManager.getConnection(_jdbcDriver, _user, _password);
                statement = _connection.createStatement();
                ResultSet resultSet = statement.executeQuery(queryString);
                relationshipsArrayList = new ArrayList<ConstructPartRelationship>();

                while (resultSet.next())
                {
                    constructID = resultSet.getString("construct_id");
                    partID = resultSet.getString("part_id");
                    relationship = new ConstructPartRelationship(constructID, partID);
                    relationshipsArrayList.add(relationship);
                }

                if(format != null && format.length() > 0)
                {
                    if(format.equalsIgnoreCase("json"))
                    {
                       responseString = this.generateJSON(relationshipsArrayList.toArray());
                       this.textSuccess(response, responseString);
                    }
                    else
                    {
                       //responseString = generateCSV(resultSet);
                       //this.textSuccess(response, responseString);

                        textError(response, "The Construct Parts web service is providing data in JSON format. CSV formatting is under development.");
                    }
                }
                else
                {
                    responseString = this.generateJSON(relationshipsArrayList.toArray());
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
           textError(response, "A Collection ID is required for requesting a set of Construct Parts.\n"
                   + "Please refer to the Data Access Web Service documentation.");
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
}
