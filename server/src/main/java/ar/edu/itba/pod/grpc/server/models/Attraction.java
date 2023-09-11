package ar.edu.itba.pod.grpc.server.models;

import ar.edu.itba.pod.grpc.server.handlers.ReservationHandler;
import ar.edu.itba.pod.grpc.server.notifications.ReservationObserver;
import ar.edu.itba.pod.grpc.server.results.AttractionAvailabilityResult;
import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.results.MakeReservationResult;
import ar.edu.itba.pod.grpc.server.utils.Constants;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Objects;

public class Attraction {
    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotDuration;
    private final ReservationHandler[] reservationHandlers;

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration, ReservationObserver reservationObserver) {
        this.name = Objects.requireNonNull(name);
        this.openingTime = Objects.requireNonNull(openingTime);
        this.closingTime = Objects.requireNonNull(closingTime);
        this.slotDuration = slotDuration;
        this.reservationHandlers = new ReservationHandler[Constants.DAYS_IN_YEAR];
        for (int i = 0; i < reservationHandlers.length; i++)
            reservationHandlers[i] = new ReservationHandler(this, i + 1, reservationObserver);
    }

    public Attraction(String name, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this(name, openingTime, closingTime, slotDuration, null);
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

    /**
     * Attempts to set slot capacity, failing if capacity is already set.
     */
    public DefineSlotCapacityResult trySetSlotCapacity(int dayOfYear, int slotCapacity) {
        ReservationHandler reservationHandler = reservationHandlers[dayOfYear - 1];
        return reservationHandler.defineSlotCapacity(slotCapacity);
    }

    /**
     * Attempts to make a reservation for a given ticket (which includes visitorId and dayOfYear) and time slot.
     */
    public MakeReservationResult tryMakeReservation(Ticket ticket, LocalTime slotTime) {
        return reservationHandlers[ticket.getDayOfYear() - 1].makeReservation(ticket, slotTime);
    }

    /**
     * Gets the availability for a given time slot.
     */
    public Collection<AttractionAvailabilityResult> getAvailabilityForAttraction(int dayOfYear, LocalTime slotFrom, LocalTime slotTo) {
        return reservationHandlers[dayOfYear - 1].getAvailability(slotFrom, slotTo);
    }

    /**
     * Gets the reservation handler for a given day of year. Only used for testing purposes
     */
    public ReservationHandler getReservationHandler(int dayOfYear) {
        return reservationHandlers[dayOfYear - 1];
    }

    /**
     * Sets the reservation handler for a given day of year. Only used for testing purposes
     */
    public void setReservationHandler(int dayOfYear, ReservationHandler reservationHandler) {
        reservationHandlers[dayOfYear - 1] = reservationHandler;
    }
}
