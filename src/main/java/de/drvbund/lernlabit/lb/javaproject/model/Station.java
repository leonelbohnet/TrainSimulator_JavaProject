package de.drvbund.lernlabit.lb.javaproject.model;

/**
 * Represents a train station in the transportation network.
 * Contains station identification, display name, map coordinates for visualization,
 * and the minimum time required for passenger transfers at this station.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class Station {
    /** Unique identifier for the station */
    private int id;
    
    /** Display name of the station */
    private String name;
    
    /** X coordinate for map visualization */
    private int x_coordinate;
    
    /** Y coordinate for map visualization */
    private int y_coordinate;
    
    /** Minimum transfer time in minutes required at this station */
    private int transfer_time_min;

    /**
     * Creates a new station with all required information.
     * 
     * @param id the unique station identifier
     * @param name the station's display name
     * @param x_coordinate the X coordinate for map display
     * @param y_coordinate the Y coordinate for map display
     * @param transfer_time_min the minimum transfer time in minutes
     */
    public Station(int id, String name, int x_coordinate, int y_coordinate, int transfer_time_min) {
        this.id = id;
        this.name = name;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
        this.transfer_time_min = transfer_time_min;
    }

    /**
     * Gets the station's unique identifier.
     * 
     * @return the station ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the station's display name.
     * 
     * @return the station name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the X coordinate for map visualization.
     * 
     * @return the X coordinate
     */
    public int getX_coordinate() {
        return x_coordinate;
    }
    
    /**
     * Gets the Y coordinate for map visualization.
     * 
     * @return the Y coordinate
     */
    public int getY_coordinate() {
        return y_coordinate;
    }
    
    /**
     * Gets the minimum transfer time at this station.
     * 
     * @return the transfer time in minutes
     */
    public int getTransfer_time_min() {
        return transfer_time_min;
    }

    /**
     * Returns the station name as the string representation.
     * Useful for displaying stations in UI components like ComboBoxes.
     * 
     * @return the station name
     */
    @Override
    public String toString() {
        return name;
    }

}
