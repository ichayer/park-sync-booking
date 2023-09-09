package ar.edu.itba.pod.grpc.server.models;

import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.results.MakeReservationResult;

import java.time.LocalTime;
import java.util.Objects;

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

    /**
     * Gets the duration of each slot, measured in minutes.
     */
    public int getSlotDuration() {
        return slotDuration;
    }

    public DefineSlotCapacityResult trySetSlotCapacity(int dayOfYear, int slotCapacity) {
        ReservationHandler reservationHandler;
        synchronized (this) {
            reservationHandler = reservationHandlers[dayOfYear - 1];
            if (reservationHandler == null) {
                reservationHandler = reservationHandlers[dayOfYear - 1] = new ReservationHandler(this, dayOfYear);
            }
        }

        return reservationHandler.defineSlotCapacity(slotCapacity);
    }

    public MakeReservationResult makeReservation(Ticket ticket, LocalTime slotTime) {
        return reservationHandlers[ticket.getDayOfYear() - 1].makeReservation(ticket, slotTime);
    }
}
