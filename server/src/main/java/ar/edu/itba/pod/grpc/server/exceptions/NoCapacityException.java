package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class NoCapacityException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.NO_CAPACITY;

    public NoCapacityException() {
        super(API_STATUS);
    }

    public NoCapacityException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public NoCapacityException(String message) {
        super(message, API_STATUS);
    }

    public NoCapacityException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}