package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class ReservationNotFoundException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.RESERVATION_NOT_FOUND;

    public ReservationNotFoundException() {
        super(API_STATUS);
    }

    public ReservationNotFoundException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public ReservationNotFoundException(String message) {
        super(message, API_STATUS);
    }

    public ReservationNotFoundException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}