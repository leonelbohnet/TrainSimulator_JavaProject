package de.drvbund.lernlabit.lb.javaproject;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class RouteOption {
    private List<RoutePart> parts;

    public RouteOption(List<RoutePart> parts) {
        this.parts = parts;
    }

    public List<RoutePart> getParts() {
        return parts;
    }

    public LocalTime getStartTime(){
        return parts.get(0).getDepartureTime();
    }

    public LocalTime getEndTime(){
        return parts.get(parts.size() - 1).getArrivalTime();
    }

    public int getTotalDuration(){
        return (int) Duration.between(getStartTime(), getEndTime()).toMinutes();
    }

    public double getTotalPrice(){
        return parts.stream().mapToDouble(RoutePart::getPrice).sum();
    }

    public int getTransferCount(){
        return parts.size() - 1;
    }
}
