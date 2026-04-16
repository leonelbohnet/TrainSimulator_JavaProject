package de.drvbund.lernlabit.lb.javaproject;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteOption {
    private List<RoutePart> parts;

    public RouteOption(List<RoutePart> parts) {
        this.parts = parts;
    }

    public List<RoutePart> getParts() {
        return parts;
    }

    public LocalTime getStartTime() {
        return parts.get(0).getDepartureTime();
    }

    public LocalTime getEndTime() {
        return parts.get(parts.size() - 1).getArrivalTime();
    }

    public int getTotalDuration() {
        return (int) Duration.between(getStartTime(), getEndTime()).toMinutes();
    }

    public double getTotalPrice() {
        return parts.stream().mapToDouble(RoutePart::getPrice).sum();
    }

    public int getTransferCount() {
        int transfers = 0;
        for (int i = 0; i < parts.size() - 1; i++) {
            RoutePart current = parts.get(i);
            RoutePart next = parts.get(i + 1);

            if (!current.getTrain_name().equals(next.getTrain_name())) {
                transfers++;
            }
        }
        return transfers;
    }

    public String getStationPath(List<Station> stations) {
        List<Integer> ids = new ArrayList<>();
        for (RoutePart part : parts) {
            ids.add(part.getFromStationId());
        }
        ids.add(parts.get(parts.size() - 1).getToStationId());

        return ids.stream()
                .map(id -> findStationName(id, stations))
                .collect(Collectors.joining(" ➔ "));
    }

    public String findStationName (int id, List<Station> stations){
        return stations.stream()
                .filter(s -> s.getId() == id)
                .map(Station::getName)
                .findFirst()
                .orElse("ID: " + id);
    }
}
