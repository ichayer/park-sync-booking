package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.UUID;

public class Ticket {

    private final UUID visitorId;
    private final int dayOfYear;
    private final TicketType ticketType;
    private int bookings;

    public Ticket(UUID visitorId, int dayOfYear, TicketType ticketType) {
        this.visitorId = visitorId;
        this.dayOfYear = dayOfYear;
        this.ticketType = ticketType;
        this.bookings = 0;
    }

    public boolean attemptBooking(LocalTime slotTime) {
        if (!canBook(slotTime)) {
            return false;
        }
        this.bookings++;
        return true;
    }

    // TODO: Check if there is any possibility for TicketTypes to contain functions that serve as conditions to
    //  determine if a reservation can be made, so that Ticket can remain agnostic to these conditions and simply
    //  call ticketType.canBook(input).
    private boolean canBook(LocalTime slotTime) {
        return switch (ticketType) {
            case FULL_DAY -> bookings < 3;
            case HALF_DAY -> slotTime.isBefore(LocalTime.of(14, 0));
            case UNLIMITED -> true;
        };
    }

    public UUID getVisitorId() {
        return visitorId;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public int getBookings() {
        return bookings;
    }
}
