package com.example.etracker_backend;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DatabaseFunctions {

    Connection connection;

    private JSONArray getJSON(ResultSet rs) {
        JSONArray json = new JSONArray();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = rsmd.getColumnName(i);
                    obj.put(column_name, rs.getObject(column_name));
                }
                json.put(obj);
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
        }
        return json;
    }

    public JSONArray getItemsLine(int index) {
        JSONArray result = new JSONArray();
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("SELECT * FROM " + SQLConnection.ITEMS_TABLE + " WHERE id=%s", index));
                ResultSet resultSet = statement.executeQuery();
                result = getJSON(resultSet);
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
        }
        return result;
    }

    public JSONArray getItems() {
        JSONArray result = new JSONArray();
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + SQLConnection.ITEMS_TABLE);
                ResultSet resultSet = statement.executeQuery();
                result = getJSON(resultSet);
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
        }
        return result;
    }

    public int deleteItem(int index) {
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("DELETE FROM " + SQLConnection.ITEMS_TABLE + " WHERE id=%s", index));
                statement.executeUpdate();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public int updateItem(String jsonString) {
        int id, revision_interval;
        String name, description, created_at, last_revised, location;
        try {
            JSONObject obj = new JSONObject(jsonString);
            id = obj.getInt("id");
            name = obj.getString("name");
            description = obj.getString("description");
            created_at = obj.getString("created_at");
            last_revised = obj.getString("last_revised");
            revision_interval = obj.getInt("revision_interval");
            location = obj.getString("location");

        } catch (JSONException e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement1 = connection.prepareStatement(String.format("SELECT * FROM " + SQLConnection.ITEMS_TABLE + " WHERE id=%s", id));
                ResultSet resultSet = statement1.executeQuery();
                if (id != resultSet.getInt(1)) {
                    Log.e("ERROR: ", "ids don't match");
                    return -1;
                }
                String updates = "";
                if (!name.equals(resultSet.getString(2))) {
                    updates += "name="+resultSet.getString(2);
                }
                if (!description.equals(resultSet.getString(3))) {
                    if (updates.length()>0)
                        updates += ", ";
                    updates += "description="+resultSet.getString(3);
                }
                if (!created_at.equals(resultSet.getString(4))) {
                    if (updates.length()>0)
                        updates += ", ";
                    updates += "created-at="+resultSet.getString(4);
                }
                if (!last_revised.equals(resultSet.getString(5))) {
                    if (updates.length()>0)
                        updates += ", ";
                    updates += "description="+resultSet.getString(5);
                }
                if (revision_interval != resultSet.getInt(6)) {
                    if (updates.length()>0)
                        updates += ", ";
                    updates += "description="+resultSet.getString(6);
                }
                if (!location.equals(resultSet.getString(7))) {
                    if (updates.length()>0)
                        updates += ", ";
                    updates += "description="+resultSet.getString(7);
                }
                PreparedStatement statement = connection.prepareStatement(String.format("UPDATE " + SQLConnection.ITEMS_TABLE + "SET " + updates + " WHERE id=%s", id));
                statement.executeUpdate();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public int createItem(String jsonString) {
        /**
         * id is not needed to create a new Item!
         */
        int revision_interval;
        String name, description, created_at, last_revised, location;
        try {
            JSONObject obj = new JSONObject(jsonString);
            name = obj.getString("name");
            description = obj.getString("description");
            created_at = obj.getString("created_at");
            last_revised = obj.getString("last_revised");
            revision_interval = obj.getInt("revision_interval");
            location = obj.getString("location");

        } catch (JSONException e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO " + SQLConnection.ITEMS_TABLE + SQLConnection.ITEM_COLUM_NAMES + String.format(SQLConnection.ITEM_COLUM_FORMAT, name, description, created_at, last_revised, revision_interval, location));
                statement.executeUpdate();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    
}
