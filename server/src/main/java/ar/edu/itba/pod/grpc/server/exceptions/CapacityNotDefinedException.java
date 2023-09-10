package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class CapacityNotDefinedException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.NO_CAPACITY;

    public CapacityNotDefinedException() {
        super(API_STATUS);
    }

    public CapacityNotDefinedException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public CapacityNotDefinedException(String message) {
        super(message, API_STATUS);
    }

    public CapacityNotDefinedException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}