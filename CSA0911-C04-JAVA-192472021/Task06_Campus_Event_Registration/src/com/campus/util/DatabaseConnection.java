package com.campus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility for managing database connections.
 * Follows security best practices:
 * - Credentials retrieved from environment variables (DB_URL, DB_USER, DB_PASSWORD)
 * - Safe fallback detection when MySQL service is offline
 */
public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/campus_events_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private static String dbUrl = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
    private static String dbUser = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
    private static String dbPass = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASS);

    private static Boolean mySqlAvailable = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // Driver may not be present in standalone execution
        }
    }

    public static void configure(String url, String user, String pass) {
        dbUrl = url;
        dbUser = user;
        dbPass = pass;
        mySqlAvailable = null; // Reset probe
    }

    public static boolean isMySqlAvailable() {
        if (mySqlAvailable != null) {
            return mySqlAvailable;
        }
        try (Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            mySqlAvailable = (c != null && !c.isClosed());
        } catch (Exception e) {
            mySqlAvailable = false;
        }
        return mySqlAvailable;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    public static String getDbUrl() { return dbUrl; }
    public static String getDbUser() { return dbUser; }
}
