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
        boolean canBook = this.ticketType.canBook(this.bookings, slotTime);
        if (canBook) {
            this.bookings++;
        }
        return canBook;
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
