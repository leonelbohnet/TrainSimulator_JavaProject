package de.drvbund.lernlabit.lb.javaproject.dataAccess;

import de.drvbund.lernlabit.lb.javaproject.controller.DatabaseManager;
import de.drvbund.lernlabit.lb.javaproject.model.RouteOption;
import de.drvbund.lernlabit.lb.javaproject.model.RoutePart;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

/**
 * Data Access Object for finding and managing route options between stations.
 * This DAO uses graph algorithms to find all possible paths between stations
 * and queries the database to find actual train schedules for each path segment.
 *
 * The class supports multi-segment journeys with transfers, calculating total
 * travel time and pricing for complete route options.
 *
 * @author Leonel Bohnet
 * @version 1.0
 */
public class RouteOptionDAO {

    /**
     * Finds all available route options between two stations starting from a specific time.
     * This method first determines all possible geographic paths between stations,
     * then finds actual train schedules for each path segment to create complete route options.
     * 
     * @param startId the ID of the starting station
     * @param endId the ID of the destination station
     * @param startTime the earliest departure time
     * @return a list of all available RouteOption objects, each representing a complete journey
     */
    public List<RouteOption> findAvailableRoutes(int startId, int endId, LocalTime startTime) {
        List<RouteOption> options = new ArrayList<>();
        
        // Load station connectivity graph from database
        Map<Integer, List<Integer>> graph = loadGraph();
        
        // Find all possible geographic paths using graph traversal
        List<List<Integer>> possiblePaths = findAllPaths(startId, endId, graph);

        // Debug output: show all found paths
        System.out.println("DEBUG: Geografische Pfade gefunden: " + possiblePaths.size());
        for(List<Integer> p : possiblePaths) System.out.println("Pfad: " + p);

        // For each path, find all possible train combinations
        for (List<Integer> path : possiblePaths) {
            findCombinations(path, 0, startTime, new ArrayList<>(), options);
        }
        return options;
    }

    /**
     * Recursively finds all valid train combinations for a given path.
     * Uses backtracking to explore all possible train connections, considering
     * transfer times between segments (5 minutes minimum).
     * 
     * @param path the list of station IDs representing the geographic path
     * @param segmentId the current segment index being processed
     * @param currentTime the earliest time for the next train departure
     * @param currentParts the list of RouteParts accumulated so far
     * @param results the collection where complete RouteOptions are added
     */
    private void findCombinations(List<Integer> path, int segmentId, LocalTime currentTime, List<RoutePart> currentParts, List<RouteOption> results) {
        // Base case: reached the end of the path, add complete route option
        if (segmentId == path.size() - 1) {
            results.add(new RouteOption(new ArrayList<>(currentParts)));
            return;
        }

        // Get current segment stations
        int from = path.get(segmentId);
        int to = path.get(segmentId + 1);

        // Find all trains available for this segment after current time
        List<RoutePart> possibleParts = getPartsForSegment(from, to, currentTime);

        // Recursively try each possible train for this segment (backtracking)
        for (RoutePart part : possibleParts) {
            currentParts.add(part);
            // Add 5 minutes transfer time before next segment
            findCombinations(path, segmentId + 1, part.getArrivalTime().plusMinutes(5), currentParts, results);
            currentParts.remove(currentParts.size() - 1); // Backtrack
        }
    }

    /**
     * Retrieves all available train RouteParts for a specific segment after a given time.
     * Queries the database for trains that travel from one station to another,
     * calculating departure/arrival times and pricing based on route schedules.
     * 
     * @param from the starting station ID for this segment
     * @param to the ending station ID for this segment
     * @param currentTime the earliest departure time to consider
     * @return a list of RoutePart objects representing available trains
     */
    private List<RoutePart> getPartsForSegment(int from, int to, LocalTime currentTime) {
        List<RoutePart> parts = new ArrayList<>();

        // Simplified test SQL (commented out, kept for reference)
        String sqlTest = "SELECT t.name AS train_name, r.route_name, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME(rs_start.offset_minutes * 60)) AS dep, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME((rs_end.offset_minutes + rs_end.duration_minutes) * 60)) AS arr, " +
                "10.0 AS price " +
                "FROM routes r " +
                "JOIN route_segments rs_start ON r.id = rs_start.route_id " +
                "JOIN route_segments rs_end ON r.id = rs_end.route_id " +
                "JOIN schedule_entries se ON r.id = se.route_id " +
                "JOIN trains t ON se.train_id = t.id " +
                "WHERE rs_start.from_station_id = ? " + // Parameter 1
                "AND rs_end.to_station_id = ? " +      // Parameter 2
                "AND rs_start.route_id = rs_end.route_id " + // Ensure same route
                "AND rs_start.stop_order <= rs_end.stop_order"; // Allow start = end segment

        // Full SQL query with price calculation
        // Calculates actual departure/arrival times by adding route offsets to schedule entry base time
        // Computes price by summing price factors of all segments between start and end
        String sql = "SELECT t.name AS train_name, r.route_name, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME(rs_start.offset_minutes * 60)) AS dep, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME((rs_end.offset_minutes + rs_end.duration_minutes) * 60)) AS arr, " +
                "(SELECT SUM(ts.price_factor) * tt.price_factor FROM route_segments rs3 " +
                "JOIN track_segments ts ON ((rs3.from_station_id = ts.station_a_id AND rs3.to_station_id = ts.station_b_id) " +
                "OR (rs3.from_station_id = ts.station_b_id AND rs3.to_station_id = ts.station_a_id)) " +
                "WHERE rs3.route_id = r.id AND rs3.stop_order BETWEEN rs_start.stop_order AND rs_end.stop_order) AS price " +
                "FROM routes r " +
                "JOIN route_segments rs_start ON r.id = rs_start.route_id " +
                "JOIN route_segments rs_end ON r.id = rs_end.route_id " +
                "JOIN schedule_entries se ON r.id = se.route_id " +
                "JOIN trains t ON se.train_id = t.id " +
                "JOIN train_types tt ON t.type_id = tt.id " +
                "WHERE rs_start.from_station_id = ? " +
                "AND rs_end.to_station_id = ? " +
                "AND rs_start.route_id = rs_end.route_id " +
                "AND rs_start.stop_order <= rs_end.stop_order " +
                "AND ADDTIME(se.departure_time, SEC_TO_TIME(rs_start.offset_minutes * 60)) >= ? " +
                "ORDER BY dep ASC";

        // Debug output
        System.out.println("DEBUG SQL: " + sql);

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set query parameters
            pstmt.setInt(1, from);
            pstmt.setInt(2, to);
            pstmt.setTime(3, Time.valueOf(LocalTime.of(0, 0))); // Currently using midnight as minimum time
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Create RoutePart objects from result set
                while (rs.next()) {
                    parts.add(new RoutePart(
                            rs.getString("train_name"),
                            rs.getString("route_name"),
                            from,
                            to,
                            rs.getTime("dep").toLocalTime(),
                            rs.getTime("arr").toLocalTime(),
                            rs.getDouble("price")
                    ));
                }
                System.out.println("DEBUG: Segment " + from + " -> " + to + ": " + parts.size() + " Züge gefunden.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parts;
    }


    /**
     * Finds all possible geographic paths between two stations using breadth-first search.
     * Limits path length to a maximum of 5 stations to prevent excessive computation.
     * Ensures no station is visited twice in the same path to avoid cycles.
     * 
     * @param start the starting station ID
     * @param end the destination station ID
     * @param graph the station connectivity graph (adjacency list)
     * @return a list of paths, where each path is a list of station IDs from start to end
     */
    private List<List<Integer>> findAllPaths(int start, int end, Map<Integer, List<Integer>> graph) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();
        
        // Initialize with starting station
        queue.add(Collections.singletonList(start));

        // Breadth-first search for all paths
        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int lastStation = path.get(path.size() - 1);

            // Check if we reached the destination
            if (lastStation == end) {
                result.add(new ArrayList<>(path));
                continue;
            }

            // Limit path length to 5 stations to avoid excessive paths
            if (path.size() < 5) {
                // Explore all neighbors of the current station
                for (int neighbor : graph.getOrDefault(lastStation, new ArrayList<>())) {
                    // Avoid cycles: don't revisit stations already in the path
                    if (!path.contains(neighbor)) {
                        List<Integer> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }
        }
        return result;
    }


    /**
     * Loads the station connectivity graph from the database.
     * Creates an undirected graph where each station maps to its connected neighbors.
     * Track segments are bidirectional, so both directions are added to the graph.
     * 
     * @return a map where keys are station IDs and values are lists of connected station IDs
     */
    private Map<Integer, List<Integer>> loadGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        String sql = "SELECT station_a_id, station_b_id FROM track_segments";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int a = rs.getInt("station_a_id");
                int b = rs.getInt("station_b_id");
                
                // Add bidirectional edges (track segments work both ways)
                graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graph;
    }

}

