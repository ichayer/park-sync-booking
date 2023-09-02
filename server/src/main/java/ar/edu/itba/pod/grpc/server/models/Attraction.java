package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

    public Optional<Integer> getCapacityByDate(LocalDate date) {
        return Optional.ofNullable(capacityByDate.get(date));
    }

    public boolean setCapacityByDate(LocalDate date, int capacity) {
        return capacityByDate.putIfAbsent(date, capacity) == null;
    }

    public boolean removeCapacityByDate(LocalDate date) {
        return capacityByDate.remove(date) != null;
    }

    public boolean updateCapacityByDate(LocalDate date, int capacity) {
        return capacityByDate.replace(date, capacity) != null;
    }

    public List<LocalDate> getDatesWithCapacitySet() {
        return new ArrayList<>(capacityByDate.keySet());
    }

    public int getAmountOfDatesWithCapacitySet() {
        return capacityByDate.size();
    }
}
