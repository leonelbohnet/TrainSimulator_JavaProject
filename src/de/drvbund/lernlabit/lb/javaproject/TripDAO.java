package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripDAO {

    static int count = 0;

    public List<AnimatedTrain> getCurrentlyRunningTrips(Map<Integer, Station> stationMap) {
        List<AnimatedTrain> activeTrains = new ArrayList<>();


        // SQL Query: Wir vergleichen nur die Uhrzeit (TIME)
        // Der Filter stellt sicher, dass der Zug bereits abgefahren, aber noch nicht angekommen ist.
        String sql = "SELECT t.name AS train_name, rs.from_station_id, rs.to_station_id, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME(rs.offset_minutes * 60)) AS segment_dep, " +
                "ADDTIME(se.departure_time, SEC_TO_TIME((rs.offset_minutes + rs.duration_minutes) * 60)) AS segment_arr " +
                "FROM schedule_entries se " +
                "JOIN trains t ON se.train_id = t.id " +
                "JOIN route_segments rs ON se.route_id = rs.route_id " +
                "WHERE (TIME_TO_SEC(TIME(?)) % 3600) " +
                "BETWEEN (TIME_TO_SEC(ADDTIME(se.departure_time, SEC_TO_TIME(rs.offset_minutes * 60))) % 3600) " +
                "AND (TIME_TO_SEC(ADDTIME(se.departure_time, SEC_TO_TIME((rs.offset_minutes + rs.duration_minutes) * 60))) % 3600)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTime(1, Time.valueOf(SimulationController.getInstance().getVirtualTime()));
            count++;
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("train_name");
                    int startId = rs.getInt("from_station_id");
                    int endId = rs.getInt("to_station_id");

                    // Umwandlung von SQL Timestamp zu Java LocalDateTime
                    Timestamp depTimestamp = rs.getTimestamp("segment_dep");
                    Timestamp arrTimestamp = rs.getTimestamp("segment_arr");

                    Station startStation = stationMap.get(startId);
                    Station endStation = stationMap.get(endId);



                    // Nur hinzufügen, wenn beide Bahnhöfe in der Map existieren
                    if (startStation != null && endStation != null) {
                        AnimatedTrain train = new AnimatedTrain(
                                name,
                                startStation,
                                endStation,
                                depTimestamp.toLocalDateTime(),
                                arrTimestamp.toLocalDateTime()
                        );
                        activeTrains.add(train);

                        System.out.println(count + ": Zug " + name + " geladen.");
                    }
                }
            }

            // Debugging Info für die Konsole
            // System.out.println("DAO: " + activeTrains.size() + " Züge für Sim-Zeit " + virtualTime + " geladen.");

        } catch (SQLException e) {
            System.err.println("Fehler beim Laden der Trips: " + e.getMessage());
            e.printStackTrace();
        }

        return activeTrains;
    }
}