package de.drvbund.lernlabit.lb.javaproject.model;

import java.time.LocalTime;

/**
 * Represents a single segment of a train journey.
 * A RoutePart contains information about one train traveling from one station
 * to another, including schedule times and pricing information.
 * 
 * Multiple RouteParts can be combined to form a complete RouteOption with transfers.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class RoutePart {
    /** The name of the train serving this segment */
    private String train_name;
    
    /** The name of the route/line this train operates on */
    private String route_name;
    
    /** The ID of the departure station */
    private int fromStationId;
    
    /** The ID of the arrival station */
    private int toStationId;
    
    /** The scheduled departure time from the start station */
    private LocalTime departureTime;
    
    /** The scheduled arrival time at the end station */
    private LocalTime arrivalTime;
    
    /** The price for this segment of the journey */
    private double price;

    /**
     * Creates a new route part representing a single train segment.
     * 
     * @param trainName the name of the train (e.g., "ICE 123", "RE 456")
     * @param routeName the name of the route/line (e.g., "ICE Berlin-München")
     * @param startStationId the ID of the departure station
     * @param endStationId the ID of the arrival station
     * @param departureTime the scheduled departure time
     * @param arrivalTime the scheduled arrival time
     * @param price the price for this segment
     */
    public RoutePart(String trainName, String routeName, int startStationId, int endStationId, LocalTime departureTime, LocalTime arrivalTime, double price) {
        route_name = routeName;
        train_name = trainName;
        this.fromStationId = startStationId;
        this.toStationId = endStationId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    /**
     * Gets the route/line name.
     * 
     * @return the route name
     */
    public String getRoute_name() {
        return route_name;
    }

    /**
     * Gets the train name.
     * 
     * @return the train name
     */
    public String getTrain_name() {
        return train_name;
    }

    /**
     * Gets the departure station ID.
     * 
     * @return the ID of the starting station
     */
    public int getFromStationId() {
        return fromStationId;
    }

    /**
     * Gets the arrival station ID.
     * 
     * @return the ID of the ending station
     */
    public int getToStationId() {
        return toStationId;
    }

    /**
     * Gets the scheduled departure time.
     * 
     * @return the departure time
     */
    public LocalTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Gets the scheduled arrival time.
     * 
     * @return the arrival time
     */
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Gets the price for this segment.
     * 
     * @return the segment price
     */
    public double getPrice() {
        return price;
    }
}
