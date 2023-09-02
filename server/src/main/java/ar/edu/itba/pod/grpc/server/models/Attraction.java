package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Attraction {

    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotDuration;
    private final Map<LocalDate, Integer> capacityByDate;

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
        this.capacityByDate = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public int getSlotDuration() {
        return slotDuration;
    }

    public int getCapacityByDate(LocalDate date) {
        return capacityByDate.getOrDefault(date, null);
    }

    public boolean setCapacityByDate(LocalDate date, int capacity) {
        return capacityByDate.putIfAbsent(date, capacity) == null;
    }

}
