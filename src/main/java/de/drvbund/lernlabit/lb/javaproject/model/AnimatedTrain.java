package de.drvbund.lernlabit.lb.javaproject.model;

import de.drvbund.lernlabit.lb.javaproject.controller.SimulationController;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a train with animated movement between stations.
 * This class tracks a train's current segment, calculates its position
 * based on simulation time, and manages multi-segment journeys.
 * 
 * The train's position is interpolated linearly between start and end stations
 * based on elapsed time and total segment duration.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class AnimatedTrain {
    /** The train's display name */
    private String name;
    
    /** Current segment's starting station */
    private Station startStation;
    
    /** Current segment's ending station */
    private Station endStation;
    
    /** Scheduled departure time from current segment's start station */
    private LocalDateTime departureTime;
    
    /** Scheduled arrival time at current segment's end station */
    private LocalDateTime arrivalTime;
    
    /** ID of the train's final destination station for the complete journey */
    private int finalStationId;

    /**
     * Creates a new animated train.
     * 
     * @param name the train's display name
     * @param startStation the current segment's starting station
     * @param endStation the current segment's ending station
     * @param departureTime scheduled departure time from start station
     * @param arrivalTime scheduled arrival time at end station
     * @param finalStationId the ID of the train's ultimate destination
     */
    public AnimatedTrain(String name, Station startStation, Station endStation, LocalDateTime departureTime, LocalDateTime arrivalTime, int finalStationId) {
        this.name = name;
        this.startStation = startStation;
        this.endStation = endStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.finalStationId = finalStationId;
    }

    /**
     * Calculates the train's progress along the current segment.
     * Uses simulation time to determine how far the train has traveled
     * between departure and arrival times.
     * 
     * Handles time wraparound for segments crossing the hour boundary
     * by adding 3600 seconds when negative durations are detected.
     * 
     * @return a value between 0.0 (at start station) and 1.0 (at end station)
     */
    public double getProgress() {
        LocalTime simTime = SimulationController.getInstance().getVirtualTime();

        // Convert times to seconds within the hour (ignoring hours for wraparound handling)
        long nowSec = (simTime.getMinute() * 60L) + simTime.getSecond();
        long depSec = (departureTime.getMinute() * 60L) + departureTime.getSecond();
        long arrSec = (arrivalTime.getMinute() * 60L) + arrivalTime.getSecond();

        // Calculate durations
        long totalDuration = arrSec - depSec;
        long elapsedTime = nowSec - depSec;

        // Handle time wraparound (crossing hour boundary)
        if (totalDuration < 0) totalDuration += 3600;
        if (elapsedTime < 0) elapsedTime += 3600;

        // Avoid division by zero for instantaneous trips
        if (totalDuration <= 0) return 1.0;
        
        // Calculate progress and clamp between 0.0 and 1.0
        double progress = (double) elapsedTime / totalDuration;
        return Math.max(0.0, Math.min(1.0, progress));
    }

    /**
     * Calculates the train's current position on the map.
     * Uses linear interpolation between start and end station coordinates
     * based on the current progress percentage.
     * 
     * @return a Point2D representing the train's current (x, y) position
     */
    public Point2D.Double getCurrentPosition() {
        double progress = getProgress();
        // Linear interpolation: current = start + progress * (end - start)
        double x = startStation.getX_coordinate() + progress * (endStation.getX_coordinate() - startStation.getX_coordinate());
        double y = startStation.getY_coordinate() + progress * (endStation.getY_coordinate() - startStation.getY_coordinate());
        return new Point2D.Double(x, y);
    }

    /**
     * Updates the train to a new segment.
     * Used when the train moves from one track segment to the next
     * in its multi-segment journey.
     * 
     * @param newStart the new segment's starting station
     * @param newEnd the new segment's ending station
     * @param newDeparture the departure time from the new start station
     * @param newArrival the arrival time at the new end station
     */
    public void updateSegment(Station newStart, Station newEnd, LocalDateTime newDeparture, LocalDateTime newArrival) {
        this.startStation = newStart;
        this.endStation = newEnd;
        this.departureTime = newDeparture;
        this.arrivalTime = newArrival;
    }

    /**
     * Checks if the train has reached its final destination.
     * A train is at its final destination when it has completed progress
     * on the segment ending at the final station.
     * 
     * @return true if the train is at the final destination, false otherwise
     */
    public boolean isAtFinalDestination() {
        return endStation.getId() == finalStationId && getProgress() >= 1.0;
    }

    /**
     * Gets the train's display name.
     * 
     * @return the train name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current segment's starting station.
     * 
     * @return the start station
     */
    public Station getStartStation() {
        return startStation;
    }

    /**
     * Gets the current segment's ending station.
     * 
     * @return the end station
     */
    public Station getEndStation() {
        return endStation;
    }

    /**
     * Gets the scheduled departure time from the current segment's start station.
     * 
     * @return the departure time
     */
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Gets the scheduled arrival time at the current segment's end station.
     * 
     * @return the arrival time
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Gets the ID of the train's final destination station.
     * 
     * @return the final station ID
     */
    public int getFinalStationId() {
        return finalStationId;
    }

}
