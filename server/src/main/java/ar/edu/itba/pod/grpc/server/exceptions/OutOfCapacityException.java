package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class OutOfCapacityException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.OUT_OF_CAPACITY;

    public OutOfCapacityException() {
        super(API_STATUS);
    }

    public OutOfCapacityException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public OutOfCapacityException(String message) {
        super(message, API_STATUS);
    }

    public OutOfCapacityException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}