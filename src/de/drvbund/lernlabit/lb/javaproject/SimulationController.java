package de.drvbund.lernlabit.lb.javaproject;

import java.time.LocalTime;

public class SimulationController {
    private static SimulationController instance;
    private LocalTime virtualTime = LocalTime.of(6, 0);
    private double speedFactor = 10.0;

    private SimulationController() {
    }

    public static SimulationController getInstance() {
        if (instance == null) {
            instance = new SimulationController();
        }
        return instance;
    }

    public void tick (long elapsedMillisReal){
        long nanosToAdd = (long) (elapsedMillisReal * 1000000 * speedFactor);
        virtualTime = virtualTime.plusNanos(nanosToAdd);
    }

    public LocalTime getVirtualTime(){
        return virtualTime;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }
}
