package de.drvbund.lernlabit.lb.javaproject.model;

public class Station {
    private int id;
    private String name;
    private int x_coordinate;
    private int y_coordinate;
    private int transfer_time_min;


    public Station(int id, String name, int x_coordinate, int y_coordinate, int transfer_time_min){
        this.id = id;
        this.name = name;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
        this.transfer_time_min = transfer_time_min;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getX_coordinate() {
        return x_coordinate;
    }
    public int getY_coordinate() {
        return y_coordinate;
    }
    public int getTransfer_time_min() {
        return transfer_time_min;
    }

    @Override
    public String toString() {
        return name;
    }

}
