package de.drvbund.lernlabit.lb.javaproject.model;

import java.time.LocalTime;

public class RoutePart {
    private String train_name;
    private String route_name;
    private int fromStationId;
    private int toStationId;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private double price;


    public RoutePart(String trainName, String routeName, int startStationId, int endStationId, LocalTime departureTime, LocalTime arrivalTime, double price) {
        route_name = routeName;
        train_name = trainName;
        this.fromStationId = startStationId;
        this.toStationId = endStationId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getTrain_name() {
        return train_name;
    }

    public int getFromStationId() {
        return fromStationId;
    }

    public int getToStationId() {
        return toStationId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }
}
