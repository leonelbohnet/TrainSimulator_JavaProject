package de.drvbund.lernlabit.lb.javaproject.model;

public class TrainType {
    private int id;
    private String description;
    private String category;
    private double speedFactor;
    private double priceFactor;
    private int seatCount;

    public TrainType(int id, String description, String category, double speedFactor, double priceFactor, int seatCount) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.speedFactor = speedFactor;
        this.priceFactor = priceFactor;
        this.seatCount = seatCount;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public double getPriceFactor() {
        return priceFactor;
    }

    public int getSeatCount() {
        return seatCount;
    }
}
