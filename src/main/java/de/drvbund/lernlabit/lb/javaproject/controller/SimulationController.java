package de.drvbund.lernlabit.lb.javaproject.controller;

import java.time.LocalTime;

/**
 * Controls the simulation time and speed for the train simulator.
 * This singleton class manages a virtual clock that runs independently from real time,
 * allowing the simulation to run faster or slower than real-time.
 * 
 * The controller supports pause/resume functionality and provides callbacks
 * for each simulation tick to update dependent components.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class SimulationController {
    /** Singleton instance of SimulationController */
    private static SimulationController instance;
    
    /** Current virtual time in the simulation, initialized to 18:00 */
    private LocalTime virtualTime = LocalTime.of(18, 0);
    
    /** Speed multiplier for simulation time (10.0 = 10x real-time speed) */
    private double speedFactor = 10.0;
    
    /** Temporary storage for speedFactor when simulation is paused */
    private double tmpSpeedFactor = speedFactor;
    
    /** Callback executed on each simulation tick */
    private Runnable onTickCallback;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private SimulationController() {
    }

    /**
     * Returns the singleton instance of SimulationController.
     * Creates the instance if it doesn't exist yet (lazy initialization).
     * 
     * @return the singleton SimulationController instance
     */
    public static SimulationController getInstance() {
        if (instance == null) {
            instance = new SimulationController();
        }
        return instance;
    }

    /**
     * Advances the virtual time based on real elapsed time and speed factor.
     * Called on each frame/tick of the simulation to update the virtual clock.
     * After updating time, executes the registered callback if one exists.
     * 
     * @param elapsedMillisReal milliseconds elapsed in real time since last tick
     */
    public void tick(long elapsedMillisReal) {
        // Convert elapsed real milliseconds to nanoseconds and apply speed factor
        long nanosToAdd = (long) (elapsedMillisReal * 1000000 * speedFactor);
        virtualTime = virtualTime.plusNanos(nanosToAdd);
        
        // Notify listeners of the time update
        if (onTickCallback != null) {
            onTickCallback.run();
        }
    }

    /**
     * Registers a callback to be executed on each simulation tick.
     * This allows other components to synchronize with simulation time updates.
     * 
     * @param callback the Runnable to execute on each tick
     */
    public void setOnTickListener(Runnable callback) {
        this.onTickCallback = callback;
    }

    /**
     * Pauses the simulation by setting speed factor to 0.
     * The current speed factor is saved to allow resuming at the same speed.
     */
    public void pauseSimTime() {
        tmpSpeedFactor = speedFactor;
        speedFactor = 0;
    }

    /**
     * Resumes the simulation by restoring the previous speed factor.
     * The simulation continues from the current virtual time.
     */
    public void resumeSimTime() {
        speedFactor = tmpSpeedFactor;
    }

    /**
     * Returns the current virtual time in the simulation.
     * 
     * @return the current LocalTime in the simulation
     */
    public LocalTime getVirtualTime() {
        return virtualTime;
    }

    /**
     * Sets the speed factor for the simulation.
     * A value of 1.0 means real-time, 10.0 means 10x faster than real-time.
     * 
     * @param speedFactor the new speed multiplier (must be positive)
     */
    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    /**
     * Returns the current speed factor as an integer.
     * 
     * @return the speed factor rounded down to the nearest integer
     */
    public int getSpeedFactor() {
        return (int) speedFactor;
    }
}
