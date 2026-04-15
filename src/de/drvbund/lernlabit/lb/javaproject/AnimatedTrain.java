package de.drvbund.lernlabit.lb.javaproject;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalTime;

public class AnimatedTrain {
    private String name;
    private Station startStation;
    private Station endStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    public AnimatedTrain(String name, Station startStation, Station endStation, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.name = name;
        this.startStation = startStation;
        this.endStation = endStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;

    }

//    public double getProgress() {
//        LocalTime now = LocalTime.now();
//        LocalTime dep = departureTime.toLocalTime();
//        LocalTime arr = arrivalTime.toLocalTime();
//
//
//        if (now.isBefore(dep)) return 0.0;
//        if (now.isAfter(arr)) return 1.0;
//
//        long totalDuration = Duration.between(dep, arr).toMillis();
//        long elapsedDuration = Duration.between(dep, now).toMillis();
//
//        if (totalDuration <= 0) return 1.0;
//
//        double scaledElapsed = (double) elapsedDuration * 60;
//
//        double progress = (double) (scaledElapsed * 60) / totalDuration;
//
//        return Math.max(0.0, Math.min(1.0, progress));
//    }

    public double getProgress() {
        LocalTime now = LocalTime.now();

        long nowInHour = (now.getMinute() * 60) + now.getSecond();

        LocalTime dep = departureTime.toLocalTime();
        LocalTime arr = arrivalTime.toLocalTime();

        long depInHour = (departureTime.getMinute() * 60) + departureTime.getSecond();
        long arrInHour = (arrivalTime.getMinute() * 60) + arrivalTime.getSecond();

        if (nowInHour < depInHour) return 0.0;
        if (nowInHour > arrInHour) return 1.0;

        long totalDuration = arrInHour - depInHour;
        long elapsed = nowInHour - depInHour;

        if (totalDuration <= 0) return 1.0;

        // 4. Skalierung (Faktor 60)
        // Da wir jetzt in "Simulator-Minuten" rechnen, ist der Fortschritt:
        double progress = (double) (elapsed * 1) / totalDuration;

        return Math.max(0.0, Math.min(1.0, progress));
    }

    public Point2D.Double getCurrentPosition() {
        double progress = getProgress();
        double x = startStation.getX_coordinate() + progress * (endStation.getX_coordinate() - startStation.getX_coordinate());
        double y = startStation.getY_coordinate() + progress * (endStation.getY_coordinate() - startStation.getY_coordinate());
        return new Point2D.Double(x, y);
    }

    public String getName() {
        return name;
    }

}
