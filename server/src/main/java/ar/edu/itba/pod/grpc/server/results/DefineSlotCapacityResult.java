package ar.edu.itba.pod.grpc.server.results;

/**
 * Represents the result of a set slot capacity request.
 */
public record DefineSlotCapacityResult( int bookingsConfirmed, int bookingsRelocated, int bookingsCancelled) {
}
