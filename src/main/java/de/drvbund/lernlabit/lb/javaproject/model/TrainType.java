package de.drvbund.lernlabit.lb.javaproject.model;

/**
 * Represents a classification of trains with shared operational characteristics.
 * Train types define categories like ICE (high-speed), Regional Express, etc.,
 * each with specific speed factors, pricing multipliers, and seating capacity.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class TrainType {
    /** Unique identifier for the train type */
    private int id;
    
    /** Descriptive name of the train type (e.g., "ICE", "Regional Express") */
    private String description;
    
    /** Category classification (e.g., "high-speed", "regional") */
    private String category;
    
    /** Speed multiplier for this train type (affects travel time calculations) */
    private double speedFactor;
    
    /** Price multiplier for this train type (affects journey pricing) */
    private double priceFactor;
    
    /** Maximum passenger seating capacity for this train type */
    private int seatCount;

    /**
     * Creates a new train type with all operational characteristics.
     * 
     * @param id the unique train type identifier
     * @param description the descriptive name of this train type
     * @param category the category classification
     * @param speedFactor the speed multiplier (higher = faster)
     * @param priceFactor the pricing multiplier (higher = more expensive)
     * @param seatCount the maximum seating capacity
     */
    public TrainType(int id, String description, String category, double speedFactor, double priceFactor, int seatCount) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.speedFactor = speedFactor;
        this.priceFactor = priceFactor;
        this.seatCount = seatCount;
    }

    /**
     * Gets the train type's unique identifier.
     * 
     * @return the train type ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the descriptive name of the train type.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the category classification.
     * 
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the speed factor for this train type.
     * Higher values indicate faster trains.
     * 
     * @return the speed factor
     */
    public double getSpeedFactor() {
        return speedFactor;
    }

    /**
     * Gets the price factor for this train type.
     * Used as a multiplier in journey cost calculations.
     * 
     * @return the price factor
     */
    public double getPriceFactor() {
        return priceFactor;
    }

    /**
     * Gets the maximum seating capacity.
     * 
     * @return the seat count
     */
    public int getSeatCount() {
        return seatCount;
    }
}
