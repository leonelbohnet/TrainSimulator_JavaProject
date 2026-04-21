package de.drvbund.lernlabit.lb.javaproject.dataAccess;

import de.drvbund.lernlabit.lb.javaproject.controller.DatabaseManager;
import de.drvbund.lernlabit.lb.javaproject.model.TrackSegment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing TrackSegment entities.
 * Provides methods to retrieve track segment information, including
 * station connections, ICE accessibility, and pricing factors.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class TrackSegmentDAO {
    
    /**
     * Retrieves all track segments from the database.
     * Each segment connects two stations and includes information about
     * whether ICE trains are allowed and the pricing factor for the segment.
     * 
     * @return a list of all TrackSegment objects from the database, or an empty list if query fails
     */
    public List<TrackSegment> getAllSegment() {
        List<TrackSegment> segments = new ArrayList<>();
        String query = "SELECT * FROM track_segments";
        
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            // Create TrackSegment objects from result set
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

