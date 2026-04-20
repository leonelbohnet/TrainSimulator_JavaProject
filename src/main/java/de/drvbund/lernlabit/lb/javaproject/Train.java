package de.drvbund.lernlabit.lb.javaproject;

public class Train {
    private int id;
    private String name;
    private TrainType type;

    public Train(int id, String name, TrainType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TrainType getType() {
        return type;
    }
}
