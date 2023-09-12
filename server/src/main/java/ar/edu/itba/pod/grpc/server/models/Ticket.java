package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a ticket, or pass, for a visitor on a given day.
 * @implNote This class is not thread-safe. The only field that is not readonly is 'bookings', and therefore any access
 * to the related methods (canBook, addBook, removeBook) is expected to be done with the caller ensuring thread-safety.
 */
public class Ticket {
    private final UUID visitorId;
    private final int dayOfYear;
    private final TicketType ticketType;
    private int bookings;

    public Ticket(UUID visitorId, int dayOfYear, TicketType ticketType) {
        this.visitorId = Objects.requireNonNull(visitorId);
        this.ticketType = Objects.requireNonNull(ticketType);
        this.dayOfYear = dayOfYear;
        this.bookings = 0;
    }

    public boolean canBook(LocalTime slotTime) {
        return this.ticketType.canBook(this.bookings, slotTime);
    }

    public void addBook() {
        this.bookings++;
    }

    public void removeBook(LocalTime slotTime) {
        this.bookings--;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return dayOfYear == ticket.dayOfYear && bookings == ticket.bookings && Objects.equals(visitorId, ticket.visitorId) && ticketType == ticket.ticketType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorId, dayOfYear, ticketType, bookings);
    }
}
