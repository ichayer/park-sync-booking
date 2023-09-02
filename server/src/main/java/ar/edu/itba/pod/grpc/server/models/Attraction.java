package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;

public class Attraction {

    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotDuration;
    private int capacity;

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
