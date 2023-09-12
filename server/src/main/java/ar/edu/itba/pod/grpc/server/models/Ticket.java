package ar.edu.itba.pod.grpc.server.models;

import ar.edu.itba.pod.grpc.server.exceptions.MissingPassException;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Represents a ticket, or pass, for a visitor on a given day.
 *
 * @implNote The bookTransactional() and removeBook() methods are thread-safe and work as atomic operations
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

    public Ticket(UUID visitorId, int dayOfYear, TicketType ticketType, int bookings) {
        this(visitorId, dayOfYear, ticketType);
        this.bookings = bookings;
    }

    /**
     * Attempts to book a new reservation, incrementing the bookings counter and running a function all as an atomic
     * operation. If the transaction function returns a non-null value, it is assumed that the reservation succeeded,
     * so the bookings counter is incremented and the transaction function's result is returned. If the transaction
     * function returns null, it is assumed that the reservation failed, so the bookings counter isn't incremented and
     * null is returned.
     * @param slotTime The time slot for the reservation.
     * @param transaction The function that makes the reservation.
     * @return The value returned by the transaction.
     * @param <T> The return type for the transaction.
     * @throws MissingPassException If the time slot isn't allowed or the user has reached their booking limit.
     */
    public synchronized <T> T bookTransactional(LocalTime slotTime, Supplier<T> transaction) {
        if (!this.ticketType.canBook(this.bookings, slotTime))
            throw new MissingPassException();
        T result = transaction.get();
        if (result != null)
            this.bookings++;
        return result;
    }

    /**
     * Decrements the bookings counter for this ticket as an atomic operation.
     */
    public synchronized void removeBook() {
        if (this.bookings <= 0)
            throw new IllegalStateException("Cannot removeBook() when bookings is not greater than zero: " + this.bookings);
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
