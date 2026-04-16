package de.drvbund.lernlabit.lb.javaproject;

import java.time.LocalTime;
import java.util.List;

public class RoutePart {
    private String route_name;
    private String train_name;
    private int startStationId;
    private int endStationId;
    private LocalTime departureTime;
    private LocalTime arrivalTime;


    public RoutePart(String routeName, String trainName, int startStationId, int endStationId, LocalTime departureTime, LocalTime arrivalTime) {
        route_name = routeName;
        train_name = trainName;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getTrain_name() {
        return train_name;
    }

    public int getStartStationId() {
        return startStationId;
    }

    public int getEndStationId() {
        return endStationId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
}
