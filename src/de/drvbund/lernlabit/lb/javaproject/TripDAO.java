package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripDAO {


    public List<AnimatedTrain> getCurrentlyRunningTrips(Map<Integer, Station> stationMap) {
        List<AnimatedTrain> activeTrains = new ArrayList<>();

        // SQL Query: Wir vergleichen nur die Uhrzeit (TIME)
        // Der Filter stellt sicher, dass der Zug bereits abgefahren, aber noch nicht angekommen ist.
        String sql = "SELECT t.name, tr.start_station_id, tr.end_station_id, " +
                "tr.scheduled_departure, tr.scheduled_arrival " +
                "FROM trips tr " +
                "JOIN trains t ON tr.train_id = t.id " +
                "WHERE (TIME_TO_SEC(TIME(?)) % 3600) " +
                "BETWEEN (TIME_TO_SEC(TIME(tr.scheduled_departure)) % 3600) " +
                "AND (TIME_TO_SEC(TIME(tr.scheduled_arrival)) % 3600)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Wir setzen die virtuelle Zeit als Parameter für das WHERE TIME(?)
            pstmt.setTime(1, Time.valueOf(SimulationController.getInstance().getVirtualTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int startId = rs.getInt("start_station_id");
                    int endId = rs.getInt("end_station_id");

                    // Umwandlung von SQL Timestamp zu Java LocalDateTime
                    Timestamp depTimestamp = rs.getTimestamp("scheduled_departure");
                    Timestamp arrTimestamp = rs.getTimestamp("scheduled_arrival");

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