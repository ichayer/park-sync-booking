package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;

public class Attraction {

    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotDuration;
    private static final int DAYS_OF_THE_YEAR = 365;
    private final ReservationsHandler[] reservationsHandlers;

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
        this.reservationsHandlers = new ReservationsHandler[DAYS_OF_THE_YEAR];
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
        boolean isReservationHandlerUninitialized = reservationsHandlers[dayOfYear - 1] == null;
        if(isReservationHandlerUninitialized) {
            reservationsHandlers[dayOfYear - 1] = new ReservationsHandler(this.slotDuration, this.openingTime, this.closingTime);
            reservationsHandlers[dayOfYear - 1].defineSlotCapacity(slotCapacity);
        }
        return isReservationHandlerUninitialized;
    }
}
