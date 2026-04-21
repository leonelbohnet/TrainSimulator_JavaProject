package de.drvbund.lernlabit.lb.javaproject.dataAccess;

import de.drvbund.lernlabit.lb.javaproject.model.AnimatedTrain;
import de.drvbund.lernlabit.lb.javaproject.controller.DatabaseManager;
import de.drvbund.lernlabit.lb.javaproject.controller.SimulationController;
import de.drvbund.lernlabit.lb.javaproject.model.Station;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for managing train trips.
 * Retrieves currently active trips based on simulation time and creates
 * AnimatedTrain objects for visualization on the map.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class TripDAO {

    /** Counter for debugging purposes (tracks number of queries) */
    static int count = 0;

    /**
     * Retrieves all trains currently running at the current simulation time.
     * Uses the SimulationController's virtual time to determine which trains are active.
     * A train is considered active if the current time falls between its departure
     * and arrival times for any segment in its route.
     * 
     * The SQL query uses modulo arithmetic to handle time wraparound (24-hour cycle),
     * ensuring trains are correctly identified even across midnight boundaries.
     * 
     * @param stationMap a map of station IDs to Station objects for coordinate lookup
     * @return a list of AnimatedTrain objects representing currently active trains
     */
    public List<AnimatedTrain> getCurrentlyRunningTrips(Map<Integer, Station> stationMap) {
        List<AnimatedTrain> activeTrains = new ArrayList<>();

        // SQL Query: Compare only the time (TIME), not date
        // The filter ensures the train has departed but not yet arrived
        // Uses modulo arithmetic (% 3600) to handle wraparound at midnight
        // Subquery retrieves the final destination station for each train
        String sql = "SELECT t.name AS train_name, rs.from_station_id, rs.to_station_id, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME(rs.offset_minutes * 60)) AS segment_dep, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME((rs.offset_minutes + rs.duration_minutes) * 60)) AS segment_arr, " +
                "(SELECT rs2.to_station_id FROM route_segments rs2 WHERE rs2.route_id = se.route_id ORDER BY rs2.stop_order DESC LIMIT 1) AS final_station_id " +
                "FROM schedule_entries se " +
                "JOIN trains t ON se.train_id = t.id " +
                "JOIN route_segments rs ON se.route_id = rs.route_id " +
                "WHERE (TIME_TO_SEC(TIME(?)) % 3600) " +
                "BETWEEN (TIME_TO_SEC(ADDTIME(se.departure_time, SEC_TO_TIME(rs.offset_minutes * 60))) % 3600) " +
                "AND (TIME_TO_SEC(ADDTIME(se.departure_time, SEC_TO_TIME((rs.offset_minutes + rs.duration_minutes) * 60))) % 3600)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set current simulation time as query parameter
            pstmt.setTime(1, Time.valueOf(SimulationController.getInstance().getVirtualTime()));

            count++; // Increment query counter for debugging
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Extract train and station information
                    String name = rs.getString("train_name");
                    int startId = rs.getInt("from_station_id");
                    int endId = rs.getInt("to_station_id");
                    int finalStationId = rs.getInt("final_station_id");

                    // Convert SQL Timestamp to Java LocalDateTime
                    Timestamp depTimestamp = rs.getTimestamp("segment_dep");
                    Timestamp arrTimestamp = rs.getTimestamp("segment_arr");

                    // Look up station objects from the provided map
                    Station startStation = stationMap.get(startId);
                    Station endStation = stationMap.get(endId);

                    // Only add train if both stations exist in the map
                    // This prevents errors from missing or invalid station references
                    if (startStation != null && endStation != null) {
                        AnimatedTrain train = new AnimatedTrain(
                                name,
                                startStation,
                                endStation,
                                depTimestamp.toLocalDateTime(),
                                arrTimestamp.toLocalDateTime(),
                                finalStationId
                        );
                        activeTrains.add(train);

                        // Optional debug output (commented out)
                        // System.out.println(count + ": Zug " + name + " geladen.");
                    }
                }
            }

            // Optional debug info for console (commented out)
            // System.out.println("DAO: " + activeTrains.size() + " Züge für Sim-Zeit " + virtualTime + " geladen.");

        } catch (SQLException e) {
            System.err.println("Fehler beim Laden der Trips: " + e.getMessage());
            e.printStackTrace();
        }

        return activeTrains;
    }
}