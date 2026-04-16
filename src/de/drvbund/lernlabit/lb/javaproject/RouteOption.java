package de.drvbund.lernlabit.lb.javaproject;

import java.time.LocalTime;
import java.util.List;

public class RouteOption {
    private String route_name;
    private String train_name;
    private List<Integer> stationSIds;
    private int totalDuration;
    private double totalPrice;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public RouteOption(String route_name, String train_name, List<Integer> stationSIds, int totalDuration, double totalPrice, LocalTime departureTime, LocalTime arrivalTime) {
        this.route_name = route_name;
        this.train_name = train_name;
        this.stationSIds = stationSIds;
        this.totalDuration = totalDuration;
        this.totalPrice = totalPrice;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getTrain_name() {
        return train_name;
    }
    public List<Integer> getStationSIds() {
        return stationSIds;
    }
    public int getTotalDuration() {
        return totalDuration;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public LocalTime getDepartureTime() {
        return departureTime;
    }
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

}
