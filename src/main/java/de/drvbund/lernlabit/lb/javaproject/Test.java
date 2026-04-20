package de.drvbund.lernlabit.lb.javaproject;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Test {
    public static void main(String[] args) {
        generateSQL();
    }

    public static void generateSQL() {
        // Definition der erlaubten Strecken (IDs aus deinem Screenshot)
        String[] iceSegments = {"1,8", "8,2", "8,4", "2,6", "6,10", "3,4", "4,7", "4,9", "9,5", "7,5", "6,9"};
        String[] regioSegments = {"3,8", "1,8", "8,2", "8,4", "2,6", "6,10", "3,4", "4,7", "4,9", "9,5", "7,5", "6,9"};

        // DateTimeFormatter für das korrekte MySQL-Format
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startDateTime = LocalDateTime.of(2026, 4, 15, 8, 0, 0);

        for (int i = 0; i < 1200; i++) {
            boolean isIceTurn = (i % 2 == 0);
            String seg = isIceTurn ? iceSegments[i % iceSegments.length] : regioSegments[i % regioSegments.length];
            String[] ids = seg.split(",");
            int trainId = isIceTurn ? (i % 5 + 1) : (i % 5 + 6);

            LocalDateTime departure = startDateTime.plusSeconds(i * 30);
            LocalDateTime arrival = departure.plusMinutes(10); // 1,5 Std Simulatorzeit = 1,5 Min Realzeit

            System.out.printf("INSERT INTO trips (train_id, start_station_id, end_station_id, scheduled_departure, scheduled_arrival) " +
                            "VALUES (%d, %s, %s, '%s', '%s');\n",
                    trainId, ids[0], ids[1], departure.format(formatter), arrival.format(formatter));
        }
    }
}
