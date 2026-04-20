package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TrackSegmentDAO {
    public List<TrackSegment> getAllSegment() {
        List<TrackSegment> segments = new ArrayList<>();
        String query = "SELECT * FROM track_segments";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                segments.add(new TrackSegment(
                        resultSet.getInt("id"),
                        resultSet.getInt("station_a_id"),
                        resultSet.getInt("station_b_id"),
                        resultSet.getBoolean("is_ice_allowed"),
                        resultSet.getDouble("price_factor")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return segments;
    }
}

