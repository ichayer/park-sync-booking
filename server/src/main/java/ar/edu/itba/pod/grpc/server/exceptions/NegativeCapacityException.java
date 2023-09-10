package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class NegativeCapacityException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.NEGATIVE_CAPACITY;

    public NegativeCapacityException() {
        super(API_STATUS);
    }

    public NegativeCapacityException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public NegativeCapacityException(String message) {
        super(message, API_STATUS);
    }

    public NegativeCapacityException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}