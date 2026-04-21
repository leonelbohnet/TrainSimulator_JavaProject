package de.drvbund.lernlabit.lb.javaproject.dataAccess;

import de.drvbund.lernlabit.lb.javaproject.controller.DatabaseManager;
import de.drvbund.lernlabit.lb.javaproject.model.Station;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing Station entities.
 * Provides methods to retrieve station information from the database,
 * including coordinates for map visualization and transfer times.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class StationDAO {
    
    /**
     * Retrieves all stations from the database.
     * Loads complete station information including ID, name, coordinates, and transfer times.
     * 
     * @return a list of all Station objects from the database, or an empty list if query fails
     */
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        String query = "SELECT * FROM stations";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Create Station objects from result set
            while (resultSet.next()) {
                stations.add(new Station(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("x_coordinate"),
                        resultSet.getInt("y_coordinate"),
                        resultSet.getInt("transfer_time_min")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stations;
    }

}
