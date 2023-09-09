package ar.edu.itba.pod.grpc.server.models;

/**
 * Represents the result of a make reservation request.
 */
public record MakeReservationResult(MakeReservationStatus status, Reservation reservation) {
    public static final MakeReservationResult ALREADY_EXISTS = new MakeReservationResult(MakeReservationStatus.ALREADY_EXISTS, null);
    public static final MakeReservationResult OUT_OF_CAPACITY = new MakeReservationResult(MakeReservationStatus.OUT_OF_CAPACITY, null);
    public static final MakeReservationResult INVALID_SLOT_TIME = new MakeReservationResult(MakeReservationStatus.INVALID_SLOT_TIME, null);
    public static final MakeReservationResult ATTRACTION_NOT_FOUND = new MakeReservationResult(MakeReservationStatus.ATTRACTION_NOT_FOUND, null);
    public static final MakeReservationResult MISSING_PASS = new MakeReservationResult(MakeReservationStatus.MISSING_PASS, null);

    public boolean isSuccess() {
        return status == MakeReservationStatus.QUEUED || status == MakeReservationStatus.CONFIRMED;
    }

    /**
     * Represents the possible result statuses of a make reservation request.
     */
    public enum MakeReservationStatus {
        QUEUED,
        CONFIRMED,
        ALREADY_EXISTS,
        OUT_OF_CAPACITY,
        ATTRACTION_NOT_FOUND,
        INVALID_SLOT_TIME,
        MISSING_PASS
    }
}
