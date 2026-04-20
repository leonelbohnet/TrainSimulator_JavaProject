package de.drvbund.lernlabit.lb.javaproject.dataAccess;

import de.drvbund.lernlabit.lb.javaproject.controller.DatabaseManager;
import de.drvbund.lernlabit.lb.javaproject.model.Station;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StationDAO {
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        String query = "SELECT * FROM stations";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

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
