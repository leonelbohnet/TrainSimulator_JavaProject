package de.drvbund.lernlabit.lb.javaproject.model;

/**
 * Represents an individual train in the transportation system.
 * Each train has a unique identifier, name, and belongs to a specific train type
 * that determines its operational characteristics (speed, pricing, capacity).
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class Train {
    /** Unique identifier for the train */
    private int id;
    
    /** Display name of the train (e.g., "ICE 123", "RE 456") */
    private String name;
    
    /** The type category this train belongs to (ICE, Regional, etc.) */
    private TrainType type;

    /**
     * Creates a new train with specified properties.
     * 
     * @param id the unique train identifier
     * @param name the train's display name
     * @param type the train type defining operational characteristics
     */
    public Train(int id, String name, TrainType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the train's unique identifier.
     * 
     * @return the train ID
     */
    public int getId() {
        return id;
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
     * Gets the train's type classification.
     * 
     * @return the TrainType object
     */
    public TrainType getType() {
        return type;
    }
}
