package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;


public class TripDAO {
    public List<AnimatedTrain> getCurrentlyRunningTrips(Map<Integer, Station> stationMap) {
        List<AnimatedTrain> activeTrains = new ArrayList<>();

        String sql = "SELECT t.name, tr.start_station_id, tr.end_station_id, " +
                "tr.scheduled_departure, tr.scheduled_arrival " +
                "FROM trips tr JOIN trains t ON tr.train_id = t.id " +
                "WHERE TIME(?) BETWEEN TIME(tr.scheduled_departure) AND TIME(tr.scheduled_arrival)";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTime(1, Time.valueOf(LocalDateTime.now().toLocalTime()));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("Zug gefunden: " + resultSet.getString("name")); // Debug-Ausgabe

                    Station start = stationMap.get(resultSet.getInt("start_station_id"));
                    Station end = stationMap.get(resultSet.getInt("end_station_id"));

                    if (start != null && end != null) {
                        AnimatedTrain at = new AnimatedTrain(
                                resultSet.getString("name"),
                                start,
                                end,
                                resultSet.getTimestamp("scheduled_departure").toLocalDateTime(),
                                resultSet.getTimestamp("scheduled_arrival").toLocalDateTime()
                        );
                        activeTrains.add(at);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeTrains;
    }
}
