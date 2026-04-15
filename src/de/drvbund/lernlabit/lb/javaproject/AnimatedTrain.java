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

    public double getProgress() {
        LocalTime simTime = SimulationController.getInstance().getVirtualTime();

        // Aktuelle Position in der Stunde (0 - 3599 Sekunden)
        long nowInHour = (simTime.getMinute() * 60) + simTime.getSecond();

        LocalTime dep = departureTime.toLocalTime();
        LocalTime arr = arrivalTime.toLocalTime();

        // Geplante Zeiten in der Stunde
        long depInHour = (dep.getMinute() * 60) + dep.getSecond();
        long arrInHour = (arr.getMinute() * 60) + arr.getSecond();

        // Falls die aktuelle Sim-Zeit noch vor der Abfahrtsminute liegt
        if (nowInHour < depInHour) return 0.0;
        // Falls sie schon nach der Ankunftsminute liegt
        if (nowInHour > arrInHour) return 1.0;

        long totalDuration = arrInHour - depInHour;
        long elapsed = nowInHour - depInHour;

        if (totalDuration <= 0) return 1.0;

        // Fortschritt berechnen (Die Beschleunigung steckt bereits im SimulationController)
        return (double) elapsed / totalDuration;
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
