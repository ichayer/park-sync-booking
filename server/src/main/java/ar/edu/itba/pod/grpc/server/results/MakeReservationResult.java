package ar.edu.itba.pod.grpc.server.results;

import ar.edu.itba.pod.grpc.server.models.Reservation;

/**
 * Represents the result of a make reservation request.
 */
public record MakeReservationResult(Reservation reservation, boolean isConfirmed) {
}