package com.example.etracker_backend;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Conexiunea la baza de date
 */
public class SQLConnection {
    private static final String USERNAME = "app_login";
    private static final String IP = "192.168.68.144";
    private static final String DATABASE = "etracker";
    private static final String PASSWORD = "11774297";
    private static final String PORT = "1433";
    public static final String ITEMS_TABLE = "items";
    public static final String WORKERS_TABLE = "workers";
    public static final String REPORTS_TABLE = "reports";
    public static final String ITEM_COLUM_NAMES = " (name, description, created_at, last_revised, revision_interval, location) ";
    public static final String ITEM_COLUM_FORMAT = "(%s, %s, %s, %s, %d, %s)";

    /**
     * Metoda pentru stabilire conexiune
     *
     * @return intoarce conexiunea sau null
     */
    public static Connection getConnection() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";" + "databasename=" + DATABASE + ";user=" + USERNAME + ";password=" + PASSWORD + ";";
            connection = DriverManager.getConnection(ConnectionURL);
            if (connection == null)
                Log.e("eroare:", "bad");
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return connection;
    }
}

