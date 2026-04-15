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
        LocalTime virtualTime = SimulationController.getInstance().getVirtualTime();
        LocalTime dep = departureTime.toLocalTime();
        LocalTime arr = arrivalTime.toLocalTime();

        if (virtualTime.isBefore(dep)) return 0.0;
        if (virtualTime.isAfter(arr)) return 1.0;

        long total = Duration.between(dep, arr).toMillis();
        long elapsed = Duration.between(dep, virtualTime).toMillis();

        return (double) elapsed / total;
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
