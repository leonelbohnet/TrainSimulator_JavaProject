package de.drvbund.lernlabit.lb.javaproject;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Test utility class for generating SQL INSERT statements for train trips.
 * This class creates test data for the train simulator by generating
 * scheduled trips for both ICE and Regio trains on predefined track segments.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class Test {
    
    /**
     * Main method that executes the SQL generation.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        generateSQL();
    }

    /**
     * Generates SQL INSERT statements for train trips.
     * Creates 1200 trips alternating between ICE and Regio trains.
     * ICE trains use train IDs 1-5, Regio trains use IDs 6-10.
     * Trips are scheduled starting from April 15, 2026 at 8:00 AM,
     * with a new trip every 30 seconds and each trip lasting 10 minutes.
     */
    public static void generateSQL() {
        // Define allowed track segments for ICE trains (station ID pairs)
        String[] iceSegments = {"1,8", "8,2", "8,4", "2,6", "6,10", "3,4", "4,7", "4,9", "9,5", "7,5", "6,9"};
        
        // Define allowed track segments for Regio trains (station ID pairs)
        // Regio trains have access to one additional segment: "3,8"
        String[] regioSegments = {"3,8", "1,8", "8,2", "8,4", "2,6", "6,10", "3,4", "4,7", "4,9", "9,5", "7,5", "6,9"};

        // Formatter for MySQL datetime format (YYYY-MM-DD HH:MM:SS)
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Start date and time for the first trip
        LocalDateTime startDateTime = LocalDateTime.of(2026, 4, 15, 8, 0, 0);

        // Generate 1200 trips
        for (int i = 0; i < 1200; i++) {
            // Alternate between ICE (even) and Regio (odd) trains
            boolean isIceTurn = (i % 2 == 0);
            
            // Select segment based on train type, cycling through available segments
            String seg = isIceTurn ? iceSegments[i % iceSegments.length] : regioSegments[i % regioSegments.length];
            String[] ids = seg.split(",");
            
            // Assign train ID: ICE trains get IDs 1-5, Regio trains get IDs 6-10
            int trainId = isIceTurn ? (i % 5 + 1) : (i % 5 + 6);

            // Calculate departure time: new trip every 30 seconds
            LocalDateTime departure = startDateTime.plusSeconds(i * 30);
            
            // Calculate arrival time: 10 minutes after departure
            // Note: 1.5 hours simulator time = 1.5 minutes real time
            LocalDateTime arrival = departure.plusMinutes(10);

            // Output SQL INSERT statement
            System.out.printf("INSERT INTO trips (train_id, start_station_id, end_station_id, scheduled_departure, scheduled_arrival) " +
                            "VALUES (%d, %s, %s, '%s', '%s');\n",
                    trainId, ids[0], ids[1], departure.format(formatter), arrival.format(formatter));
        }
    }
}
