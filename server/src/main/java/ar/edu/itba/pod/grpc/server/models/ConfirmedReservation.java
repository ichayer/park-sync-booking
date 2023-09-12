package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalDateTime;

public class ConfirmedReservation extends Reservation {
    private final LocalDateTime dateConfirmed;

    public ConfirmedReservation(Ticket ticket, Attraction attraction, LocalDateTime dateConfirmed) {
        super(ticket, attraction);
        this.dateConfirmed = dateConfirmed;
    }

    public ConfirmedReservation(Ticket ticket, Attraction attraction) {
        this(ticket, attraction, LocalDateTime.now());
    }

    public ConfirmedReservation(Reservation reservation) {
        this(reservation.getTicket(), reservation.getAttraction());
    }

    public LocalDateTime getDateConfirmed() {
        return dateConfirmed;
    }
}
