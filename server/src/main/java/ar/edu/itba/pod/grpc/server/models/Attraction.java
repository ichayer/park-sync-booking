package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.Map;
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

    public boolean attemptToSetSlotCapacity(int dayOfYear, int slotCapacity) {
        ReservationsHandler reservation = reservationsHandlerMap.get(dayOfYear);
        return reservation != null && reservation.defineSlotCapacity(slotCapacity);
    }
}
