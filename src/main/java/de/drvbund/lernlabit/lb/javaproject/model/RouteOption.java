package de.drvbund.lernlabit.lb.javaproject.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a complete journey option consisting of one or more route segments.
 * A RouteOption aggregates multiple RoutePart objects to form a complete path
 * from origin to destination, including transfers if necessary.
 * 
 * This class provides methods to calculate total journey time, price, and transfer count,
 * as well as formatting utilities for display purposes.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class RouteOption {
    /** List of route segments that make up this complete journey */
    private List<RoutePart> parts;

    /**
     * Creates a new route option from a list of route parts.
     * 
     * @param parts the list of RoutePart objects making up this journey
     */
    public RouteOption(List<RoutePart> parts) {
        this.parts = parts;
    }

    /**
     * Gets all route parts in this option.
     * 
     * @return the list of RoutePart objects
     */
    public List<RoutePart> getParts() {
        return parts;
    }

    /**
     * Gets the departure time of the first segment.
     * 
     * @return the journey's start time
     */
    public LocalTime getStartTime() {
        return parts.get(0).getDepartureTime();
    }

    /**
     * Gets the arrival time of the last segment.
     * 
     * @return the journey's end time
     */
    public LocalTime getEndTime() {
        return parts.get(parts.size() - 1).getArrivalTime();
    }

    /**
     * Calculates and formats the total journey duration.
     * Handles journeys that cross midnight by adding a day to negative durations.
     * 
     * @return formatted duration string (e.g., "02 Std. 30 Min." or "45 Min.")
     */
    public String getFormattedDuration() {
        LocalTime startTime = getStartTime();
        LocalTime endTime = getEndTime();

        Duration duration = Duration.between(startTime, endTime);

        // Handle journeys crossing midnight
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }
        
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        // Format based on whether there are hours
        if (hours > 0) {
            return String.format("%02d Std. %02d Min.", hours, minutes);
        } else {
            return String.format("%02d Min.", minutes);
        }
    }

    /**
     * Calculates the total price for this route option.
     * Sums the prices of all route parts and applies a multiplier of 12.
     * 
     * @return the total journey price
     */
    public double getTotalPrice() {
        return parts.stream().mapToDouble(RoutePart::getPrice).sum() * 12;
    }

    /**
     * Counts the number of train transfers required for this journey.
     * A transfer occurs when consecutive route parts use different trains.
     * 
     * @return the number of transfers (0 for direct connections)
     */
    public int getTransferCount() {
        int transfers = 0;
        
        // Compare consecutive parts to detect train changes
        for (int i = 0; i < parts.size() - 1; i++) {
            RoutePart current = parts.get(i);
            RoutePart next = parts.get(i + 1);

            // Count a transfer if train names differ
            if (!current.getTrain_name().equals(next.getTrain_name())) {
                transfers++;
            }
        }
        return transfers;
    }

    /**
     * Generates a formatted string showing the complete station path.
     * Collects all stations in order (including the final destination) and
     * joins them with arrow symbols for visual clarity.
     * 
     * @param stations the list of all available stations for name lookup
     * @return a formatted path string (e.g., "Berlin ➔ Hamburg ➔ München")
     */
    public String getStationPath(List<Station> stations) {
        List<Integer> ids = new ArrayList<>();
        
        // Collect starting stations from each part
        for (RoutePart part : parts) {
            ids.add(part.getFromStationId());
        }
        
        // Add the final destination
        ids.add(parts.get(parts.size() - 1).getToStationId());

        // Map station IDs to names and join with arrows
        return ids.stream()
                .map(id -> findStationName(id, stations))
                .collect(Collectors.joining(" ➔ "));
    }

    /**
     * Finds the station name for a given station ID.
     * Searches through the provided station list and returns the name if found.
     * 
     * @param id the station ID to look up
     * @param stations the list of all available stations
     * @return the station name, or "ID: X" if not found
     */
    public String findStationName(int id, List<Station> stations) {
        return stations.stream()
                .filter(s -> s.getId() == id)
                .map(Station::getName)
                .findFirst()
                .orElse("ID: " + id);
    }
}
