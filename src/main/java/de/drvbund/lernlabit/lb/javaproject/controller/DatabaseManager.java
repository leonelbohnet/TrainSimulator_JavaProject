package de.drvbund.lernlabit.lb.javaproject.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the MySQL database connection using the Singleton pattern.
 * This class ensures only one database connection instance exists throughout
 * the application lifecycle and provides methods to access and maintain the connection.
 * 
 * The connection can be configured for either local development or remote database access.
 *
 * @author Leonel Bohnet
 * @version 1.0
 */
public class DatabaseManager {
    /** Singleton instance of DatabaseManager */
    private static DatabaseManager instance;
    
    /** Active database connection */
    private Connection connection;

    /** JDBC URL for local database connection */
    private final String url = "jdbc:mysql://localhost:3306/transport_system";
    
    /** Database username for local connection */
    private final String user = "root";
    
    /** Database password for local connection */
    private final String password = "";

    // Alternative configuration for remote database access:
    // private final String url = "jdbc:mysql://192.168.12.149:3306/fa2542leb_javaproject_trainsimulator";
    // private final String user = "fa2542leb";
    // private final String password = "Laterne2025!";

    /**
     * Private constructor to enforce Singleton pattern.
     * Establishes the initial database connection on first instantiation.
     * Prints a success message if connection is established, or prints stack trace on failure.
     */
    private DatabaseManager() {
        try {
            // Establish initial database connection
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung erfolgreich!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of DatabaseManager.
     * Creates the instance if it doesn't exist yet (lazy initialization).
     * 
     * @return the singleton DatabaseManager instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Returns the active database connection.
     * Automatically re-establishes the connection if it is null or closed.
     * 
     * @return the active Connection object, or null if connection fails
     */
    public Connection getConnection() {
        try {
            // Check if connection needs to be re-established
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}