package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Represents a reservation, whether pending or confirmed, made by a visitor with a ticket for an attraction.
 * @implNote This class implements no internal synchronization mechanisms and therefore is not thread-safe.
 */
public class Reservation {
    private final Ticket ticket;
    private final Attraction attraction;
    private LocalTime slotTime;
    private LocalDateTime dateConfirmed;

    public Reservation(Ticket ticket, Attraction attraction, LocalTime slotTime, boolean confirmed) {
        this.ticket = ticket;
        this.attraction = attraction;
        this.slotTime = slotTime;
        this.dateConfirmed = confirmed ? LocalDateTime.now() : null;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public UUID getVisitorId() {
        return ticket.getVisitorId();
    }

    public int getDayOfYear() {
        return ticket.getDayOfYear();
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public LocalTime getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(LocalTime slotTime) {
        this.slotTime = slotTime;
    }

    public LocalDateTime getDateConfirmed() {
        return dateConfirmed;
    }

    public boolean isConfirmed() {
        return dateConfirmed != null;
    }

    public void setAsConfirmed() {
        if (this.dateConfirmed != null)
            throw new IllegalStateException("This reservation is already confirmed");

        this.dateConfirmed = LocalDateTime.now();
    }
}
