package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RouteOptionDAO {

    public List<RouteOption> findAvailableRoutes(int startStationId, int endStationId, LocalTime requestedTime) {
        List<RouteOption> options = new ArrayList<>();

        System.out.println("DEBUG: Suche von ID " + startStationId + " nach ID " + endStationId);

        // Der SQL-Befehl als Java-String
        String sql = "SELECT " +
                "    r.route_name, " +
                "    t.name AS train_name, " +
                "    ADDTIME(se.departure_time, SEC_TO_TIME(rs_start.offset_minutes * 60)) AS dep_time, " +
                "    ADDTIME(se.departure_time, SEC_TO_TIME((rs_end.offset_minutes + rs_end.duration_minutes) * 60)) AS arr_time, " +
                "    ((rs_end.offset_minutes + rs_end.duration_minutes) - rs_start.offset_minutes) AS total_duration, " +
                "    (SELECT SUM(ts.price_factor) * tt.price_factor " +
                "     FROM route_segments rs3 " +
                "     JOIN track_segments ts ON (" +
                "        (rs3.from_station_id = ts.station_a_id AND rs3.to_station_id = ts.station_b_id) OR " +
                "        (rs3.from_station_id = ts.station_b_id AND rs3.to_station_id = ts.station_a_id)" +
                "     ) " +
                "     WHERE rs3.route_id = r.id " +
                "     AND rs3.stop_order BETWEEN rs_start.stop_order AND rs_end.stop_order) AS total_price, " +
                "    (SELECT GROUP_CONCAT(rs4.from_station_id ORDER BY rs4.stop_order) " +
                "     FROM route_segments rs4 " +
                "     WHERE rs4.route_id = r.id " +
                "     AND rs4.stop_order BETWEEN rs_start.stop_order AND rs_end.stop_order) AS path_ids " +
                "FROM routes r " +
                "JOIN route_segments rs_start ON r.id = rs_start.route_id " +
                "JOIN route_segments rs_end ON r.id = rs_end.route_id " +
                "JOIN schedule_entries se ON r.id = se.route_id " +
                "JOIN trains t ON se.train_id = t.id " +
                "JOIN train_types tt ON t.type_id = tt.id " +
                "WHERE rs_start.from_station_id = ? " +
                "  AND rs_end.to_station_id = ? " +
                "  AND rs_start.stop_order <= rs_end.stop_order " +
               // "AND (TIME_TO_SEC(ADDTIME(se.departure_time, SEC_TO_TIME(rs_start.offset_minutes * 60))) % 3600) >= (TIME_TO_SEC(?) % 3600)" +
                "ORDER BY total_duration ASC, total_price ASC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, startStationId);
            pstmt.setInt(2, endStationId);
          //  pstmt.setTime(3, Time.valueOf(requestedTime));



            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Zeit-Konvertierung
                    LocalTime departure = rs.getTime("dep_time").toLocalTime();
                    LocalTime arrival = rs.getTime("arr_time").toLocalTime();

                    // Pfad-Konvertierung (String "1,8,4" -> List<Integer>)
                    String rawPath = rs.getString("path_ids");
                    // Wir hängen die Ziel-Station manuell an, da GROUP_CONCAT nur die Start-Punkte der Segmente sammelt
                    rawPath += "," + endStationId;

                    List<Integer> stationIds = Arrays.stream(rawPath.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    // Neues RouteOption Objekt erstellen
                    options.add(new RouteOption(
                            rs.getString("route_name"),
                            rs.getString("train_name"),
                            stationIds,
                            rs.getInt("total_duration"),
                            rs.getDouble("total_price"),
                            departure,
                            arrival
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Routensuche: " + e.getMessage());
            e.printStackTrace();
        }

        return options;
    }

}

