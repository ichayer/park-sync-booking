package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Attraction {

    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotDuration;
    private final Map<Integer, ReservationsHandler> reservationsHandlerMap;

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
        this.reservationsHandlerMap = new ConcurrentHashMap<>();
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

//    public Optional<Integer> getCapacityByDate(Integer dayOfYear) {
//        return Optional.ofNullable(capacityByDate.get(dayOfYear));
//    }
//
//    public boolean setCapacityByDate(Integer dayOfYear, int capacity) {
//        return capacityByDate.putIfAbsent(dayOfYear, capacity) == null;
//    }
//
//    public boolean removeCapacityByDate(Integer dayOfYear) {
//        return capacityByDate.remove(dayOfYear) != null;
//    }
//
//    public boolean updateCapacityByDate(Integer dayOfYear, int capacity) {
//        return capacityByDate.replace(dayOfYear, capacity) != null;
//    }
//
//    public Set<Integer> getDatesWithCapacitySet() {
//        return Collections.unmodifiableSet(capacityByDate.keySet());
//    }
//
//    public int getAmountOfDatesWithCapacitySet() {
//        return capacityByDate.size();
//    }
}
