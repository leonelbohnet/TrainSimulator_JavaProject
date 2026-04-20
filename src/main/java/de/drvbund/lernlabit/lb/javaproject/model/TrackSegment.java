package de.drvbund.lernlabit.lb.javaproject.model;

public class TrackSegment {
    private int id;
    private int stationAID;
    private int stationBID;
    private boolean isIceAllowed;
    private double priceFactor;

    public TrackSegment(int id, int stationAID, int stationBID, boolean isIceAllowed, double priceFactor) {
        this.id = id;
        this.stationAID = stationAID;
        this.stationBID = stationBID;
        this.isIceAllowed = isIceAllowed;
        this.priceFactor = priceFactor;
    }

    public int getId() {
        return id;
    }

    public int getStationAID() {
        return stationAID;
    }

    public int getStationBID() {
        return stationBID;
    }

    public boolean getIsIceAllowed() {
        return isIceAllowed;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}
