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
    private int finalStationId;

    public AnimatedTrain(String name, Station startStation, Station endStation, LocalDateTime departureTime, LocalDateTime arrivalTime, int finalStationId) {
        this.name = name;
        this.startStation = startStation;
        this.endStation = endStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.finalStationId = finalStationId;

    }

    public double getProgress() {
        LocalTime simTime = SimulationController.getInstance().getVirtualTime();
        long nowInHour = (simTime.getMinute() * 60) + simTime.getSecond();

        LocalTime dep = departureTime.toLocalTime();
        LocalTime arr = arrivalTime.toLocalTime();

        long depInHour = (dep.getMinute() * 60) + dep.getSecond();
        long arrInHour = (arr.getMinute() * 60) + arr.getSecond();

        long totalDuration = arrInHour - depInHour;
        long elapsed = nowInHour - depInHour;

        if (totalDuration <= 0) return 1.0;

        double progress = (double) elapsed / totalDuration;

        // Fortschritt berechnen (Die Beschleunigung steckt bereits im SimulationController)
        return Math.max(0.0, Math.min(1.0, progress));
    }

    public Point2D.Double getCurrentPosition() {
        double progress = getProgress();
        double x = startStation.getX_coordinate() + progress * (endStation.getX_coordinate() - startStation.getX_coordinate());
        double y = startStation.getY_coordinate() + progress * (endStation.getY_coordinate() - startStation.getY_coordinate());
        return new Point2D.Double(x, y);
    }

    public void updateSegment(Station newStart, Station newEnd, LocalDateTime newDeparture, LocalDateTime newArrival){
        this.startStation = newStart;
        this.endStation = newEnd;
        this.departureTime = newDeparture;
        this.arrivalTime = newArrival;
    }

    public boolean isAtFinalDestination(){
        return endStation.getId() == finalStationId && getProgress() >= 1.0;
    }

    public String getName() {
        return name;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public int getFinalStationId(){
        return finalStationId;
    }

}
