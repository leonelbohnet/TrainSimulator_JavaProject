package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class RouteOptionDAO {

    public List<RouteOption> findAvailableRoutes(int startId, int endId, LocalTime startTime) {
        List<RouteOption> options = new ArrayList<>();
        Map<Integer, List<Integer>> graph = loadGraph();
        List<List<Integer>> possiblePaths = findAllPaths(startId, endId, graph);

        System.out.println("DEBUG: Geografische Pfade gefunden: " + possiblePaths.size());
        for(List<Integer> p : possiblePaths) System.out.println("Pfad: " + p);

        for (List<Integer> path : possiblePaths) {
            findCombinations(path, 0, startTime, new ArrayList<>(), options);

        }
        return options;
    }

    private void findCombinations(List<Integer> path, int segmentId, LocalTime currentTime, List<RoutePart> currentParts, List<RouteOption> results) {
        if (segmentId == path.size() - 1) {
            results.add(new RouteOption(new ArrayList<>(currentParts)));
            return;
        }

        int from = path.get(segmentId);
        int to = path.get(segmentId + 1);

        List<RoutePart> possibleParts = getPartsForSegment(from, to, currentTime);

        for (RoutePart part : possibleParts) {
            currentParts.add(part);
            findCombinations(path, segmentId + 1, part.getArrivalTime().plusMinutes(5), currentParts, results);
            currentParts.remove(currentParts.size() - 1);
        }
    }

    private List<RoutePart> getPartsForSegment(int from, int to, LocalTime currentTime) {
        List<RoutePart> parts = new ArrayList<>();

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
                "AND rs_start.route_id = rs_end.route_id " + // Sicherstellen, dass es die gleiche Linie ist
                "AND rs_start.stop_order <= rs_end.stop_order"; // Erlaubt Start = Ziel Segment


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

        // In deinem DAO vor pstmt = conn.prepareStatement(sql):
        System.out.println("DEBUG SQL: " + sql);

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, from);
            pstmt.setInt(2, to);
            pstmt.setTime(3, Time.valueOf(LocalTime.of(0, 0)));
            try (ResultSet rs = pstmt.executeQuery()) {
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


    private List<List<Integer>> findAllPaths(int start, int end, Map<Integer, List<Integer>> graph) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));

        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int lastStation = path.get(path.size() - 1);

            if (lastStation == end) {
                result.add(new ArrayList<>(path));
                continue;
            }

            if (path.size() < 5) {
                for (int neighbor : graph.getOrDefault(lastStation, new ArrayList<>())) {
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


    private Map<Integer, List<Integer>> loadGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        String sql = "SELECT station_a_id, station_b_id FROM track_segments";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int a = rs.getInt("station_a_id");
                int b = rs.getInt("station_b_id");
                graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graph;
    }

}

