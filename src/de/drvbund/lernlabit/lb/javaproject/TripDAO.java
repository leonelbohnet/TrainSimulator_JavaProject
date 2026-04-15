package de.drvbund.lernlabit.lb.javaproject;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;


public class TripDAO {
    // In TripDAO.java
    public List<AnimatedTrain> getCurrentlyRunningTrips(Map<Integer, Station> stationMap) {
        List<AnimatedTrain> activeTrains = new ArrayList<>();
        LocalTime virtualTime = SimulationController.getInstance().getVirtualTime();

        // Wir nutzen TIME(?), um die virtuelle Zeit an die DB zu senden
        String sql = "SELECT t.name, tr.start_station_id, tr.end_station_id, " +
                "tr.scheduled_departure, tr.scheduled_arrival " +
                "FROM trips tr JOIN trains t ON tr.train_id = t.id " +
                "WHERE TIME(?) BETWEEN TIME(tr.scheduled_departure) AND TIME(tr.scheduled_arrival)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Hier wird die virtuelle Zeit deiner Simulation eingesetzt
            pstmt.setTime(1, Time.valueOf(virtualTime));

            ResultSet rs = pstmt.executeQuery();
            // ... wie gehabt ...
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeTrains;
    }
}
