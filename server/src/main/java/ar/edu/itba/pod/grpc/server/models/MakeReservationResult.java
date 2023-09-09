package ar.edu.itba.pod.grpc.server.models;

/**
 * Represents the result of a make reservation request.
 */
public record MakeReservationResult(Status status, Reservation reservation) {
    public static final MakeReservationResult ALREADY_EXISTS = new MakeReservationResult(Status.ALREADY_EXISTS, null);
    public static final MakeReservationResult OUT_OF_CAPACITY = new MakeReservationResult(Status.OUT_OF_CAPACITY, null);
    public static final MakeReservationResult INVALID_SLOT_TIME = new MakeReservationResult(Status.INVALID_SLOT_TIME, null);
    public static final MakeReservationResult ATTRACTION_NOT_FOUND = new MakeReservationResult(Status.ATTRACTION_NOT_FOUND, null);
    public static final MakeReservationResult MISSING_PASS = new MakeReservationResult(Status.MISSING_PASS, null);

    public boolean isSuccess() {
        return status == Status.QUEUED || status == Status.CONFIRMED;
    }

    /**
     * Represents the possible result statuses of a make reservation request.
     */
    public enum Status {
        QUEUED,
        CONFIRMED,
        ALREADY_EXISTS,
        OUT_OF_CAPACITY,
        ATTRACTION_NOT_FOUND,
        INVALID_SLOT_TIME,
        MISSING_PASS
    }
}
