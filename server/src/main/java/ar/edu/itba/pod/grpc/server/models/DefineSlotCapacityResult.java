package ar.edu.itba.pod.grpc.server.models;

/**
 * Represents the result of a set slot capacity request.
 */
public record DefineSlotCapacityResult(Status status, int bookingsConfirmed, int bookingsRelocated, int bookingsCancelled) {
    public static final DefineSlotCapacityResult ATTRACTION_NOT_FOUND = new DefineSlotCapacityResult(Status.ATTRACTION_NOT_FOUND, 0, 0, 0);
    public static final DefineSlotCapacityResult CAPACITY_ALREADY_SET = new DefineSlotCapacityResult(Status.CAPACITY_ALREADY_SET, 0, 0, 0);

    /**
     * Represents the possible result statuses of a set slot capacity request.
     */
    public enum Status {
        SUCCESS,
        ATTRACTION_NOT_FOUND,
        CAPACITY_ALREADY_SET
    }
}
