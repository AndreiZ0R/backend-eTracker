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

    private JSONArray getLine(int index, String TABLE) {
        JSONArray result = new JSONArray();
        String query = "SELECT * FROM " + TABLE;
        if (index == -1)
            query = String.format(query + " WHERE id=%s", index);
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                result = getJSON(resultSet);
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
        }
        return result;
    }

    private int deleteFromTable(int index, String TABLE) {
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("DELETE FROM " + TABLE + " WHERE id=%s", index));
                statement.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public JSONArray getItem(int index) {
        return getLine(index, SQLConnection.ITEMS_TABLE);
    }

    public JSONArray getItems() {
        return getLine(-1, SQLConnection.ITEMS_TABLE);
    }

    public int deleteItem(int index) {
        return deleteFromTable(index, SQLConnection.ITEMS_TABLE);
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
                String updates = "";
                updates += "name="+name+", ";
                updates += "description="+description+", ";
                updates += "created-at="+created_at+", ";
                updates += "description="+last_revised+", ";
                updates += "description="+revision_interval+", ";
                updates += "description="+location;

                PreparedStatement statement = connection.prepareStatement(String.format("UPDATE " + SQLConnection.ITEMS_TABLE + " SET " + updates + " WHERE id=%s", id));
                statement.executeUpdate();
                connection.close();
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
         *
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
                PreparedStatement statement = connection.prepareStatement("INSERT INTO " + SQLConnection.ITEMS_TABLE + SQLConnection.ITEM_COLUMN_NAMES + String.format(SQLConnection.ITEM_COLUMN_FORMAT, name, description, created_at, last_revised, revision_interval, location));
                statement.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public int createWorker(String jsonString) {
        int phone, country_prefix;
        String full_name, company_name, address;
        try {
            JSONObject obj = new JSONObject(jsonString);
            full_name = obj.getString("full_name");
            company_name = obj.getString("company_name");
            phone = obj.getInt("phone");
            country_prefix = obj.getInt("country_prefix");
            address = obj.getString("address");
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO " + SQLConnection.WORKERS_TABLE + SQLConnection.WORKER_COLUMN_NAMES + " VALUES " + String.format(SQLConnection.WORKER_COLUMN_FORMAT, full_name, company_name, phone, country_prefix, address, 1));
                statement.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public int updateWorker(String jsonString) {
        int id, phone, country_prefix, active;
        String full_name, company_name, address;
        try {
            JSONObject obj = new JSONObject(jsonString);
            id = obj.getInt("id");
            full_name = obj.getString("full_name");
            company_name = obj.getString("company_name");
            phone = obj.getInt("phone");
            country_prefix = obj.getInt("country_prefix");
            address = obj.getString("address");
            active = obj.getInt("active");
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                String updates = "";
                updates += "full_name="+full_name+", ";
                updates += "company_name="+company_name+", ";
                updates += "phone="+phone+", ";
                updates += "country_prefix"+country_prefix+", ";
                updates += "address="+address+", ";
                updates += "active="+active;
                PreparedStatement statement = connection.prepareStatement(String.format("UPDATE " + SQLConnection.WORKERS_TABLE + " SET " + updates + " WHERE id=%s", id));
                statement.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public JSONArray getWorker(int index) {
        return getLine(index, SQLConnection.WORKERS_TABLE);
    }

    public JSONArray getWorkers() {
        return getLine(-1, SQLConnection.WORKERS_TABLE);
    }

    public int createReport(String jsonString) {
        int item_id, worker_id;
        String type, date, link;
        try {
            JSONObject obj = new JSONObject(jsonString);
            item_id = obj.getInt("item_id");
            worker_id = obj.getInt("worker_id");
            type = obj.getString("type");
            date = obj.getString("date");
            link = obj.getString("link");
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO " + SQLConnection.REPORTS_TABLE + SQLConnection.REPORT_COLUMN_NAMES + " VALUES " + String.format(SQLConnection.REPORT_COLUMN_FORMAT, item_id, worker_id, type, date, link));
                statement.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }

    public JSONArray getReport(int index) {
        return getLine(index, SQLConnection.REPORTS_TABLE);
    }

    public JSONArray getReports() {
        return getLine(-1, SQLConnection.REPORTS_TABLE);
    }

    public int deleteReport(int index) {
        return deleteFromTable(index, SQLConnection.REPORTS_TABLE);
    }
    
    public int deleteReportsOfItem(int id) {
        try {
            connection = SQLConnection.getConnection();
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(String.format("DELETE FROM " + SQLConnection.ITEMS_TABLE + " WHERE item_id=%s", id));
                statement.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            Log.e("ERROR: ", e.toString());
            return -1;
        }
        return 1;
    }
}
