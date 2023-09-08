package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class Attraction {
    private static final int DAYS_OF_THE_YEAR = 365;

    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotDuration;
    private final ReservationHandler[] reservationHandlers;

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this.name = Objects.requireNonNull(name);
        this.openingTime = Objects.requireNonNull(openingTime);
        this.closingTime = Objects.requireNonNull(closingTime);
        this.slotDuration = slotDuration;
        this.reservationHandlers = new ReservationHandler[DAYS_OF_THE_YEAR];
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

    public synchronized boolean attemptToSetSlotCapacity(int dayOfYear, int slotCapacity) {
        boolean isReservationHandlerUninitialized = reservationHandlers[dayOfYear - 1] == null;
        if(isReservationHandlerUninitialized) {
            reservationHandlers[dayOfYear - 1] = new ReservationHandler(this.slotDuration, this.openingTime, this.closingTime);
            reservationHandlers[dayOfYear - 1].defineSlotCapacity(slotCapacity);
        }
        return isReservationHandlerUninitialized;
    }

    public boolean isSlotTimeValid(int dayOfYear, LocalTime slotTime) {
        return reservationHandlers[dayOfYear - 1] != null && reservationHandlers[dayOfYear - 1].isSlotTimeValid(slotTime);
    }

    public ReservationHandler.MakeReservationResult makeReservation(UUID visitorId, int dayOfYear, LocalTime slotTime) {
        return reservationHandlers[dayOfYear - 1].makeReservation(visitorId, slotTime);
    }
}
