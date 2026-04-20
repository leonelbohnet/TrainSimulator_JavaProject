package de.drvbund.lernlabit.lb.javaproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Managed die Verbindung zur MySQL Datenbank.
 *
 * @author Leonel Bohnet
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private final String url = "jdbc:mysql://192.168.12.149:3306/fa2542leb_javaproject_trainsimulator";
    private final String user = "fa2542leb";
    private final String password = "Laterne2025!";

    private DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung erfolgreich!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}