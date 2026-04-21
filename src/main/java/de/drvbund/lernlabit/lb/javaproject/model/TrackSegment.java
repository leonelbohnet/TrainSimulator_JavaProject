package de.drvbund.lernlabit.lb.javaproject.model;

/**
 * Represents a physical track segment connecting two stations.
 * Track segments are bidirectional connections that define the railway network topology.
 * Each segment has properties determining train type restrictions and pricing.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class TrackSegment {
    /** Unique identifier for the track segment */
    private int id;
    
    /** ID of the first station connected by this segment */
    private int stationAID;
    
    /** ID of the second station connected by this segment */
    private int stationBID;
    
    /** Flag indicating whether ICE (high-speed) trains can use this segment */
    private boolean isIceAllowed;
    
    /** Price factor used in calculating journey costs for this segment */
    private double priceFactor;

    /**
     * Creates a new track segment connecting two stations.
     * 
     * @param id the unique segment identifier
     * @param stationAID the ID of the first connected station
     * @param stationBID the ID of the second connected station
     * @param isIceAllowed whether ICE trains are allowed on this segment
     * @param priceFactor the pricing factor for this segment
     */
    public TrackSegment(int id, int stationAID, int stationBID, boolean isIceAllowed, double priceFactor) {
        this.id = id;
        this.stationAID = stationAID;
        this.stationBID = stationBID;
        this.isIceAllowed = isIceAllowed;
        this.priceFactor = priceFactor;
    }

    /**
     * Gets the segment's unique identifier.
     * 
     * @return the segment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the first connected station.
     * 
     * @return the station A ID
     */
    public int getStationAID() {
        return stationAID;
    }

    /**
     * Gets the ID of the second connected station.
     * 
     * @return the station B ID
     */
    public int getStationBID() {
        return stationBID;
    }

    /**
     * Checks whether ICE trains are allowed on this segment.
     * Some tracks may only support regional trains due to infrastructure limitations.
     * 
     * @return true if ICE trains can use this segment, false otherwise
     */
    public boolean getIsIceAllowed() {
        return isIceAllowed;
    }

    /**
     * Gets the price factor for this segment.
     * Used in calculating the total journey cost.
     * 
     * @return the price factor
     */
    public double getPriceFactor() {
        return priceFactor;
    }
}
