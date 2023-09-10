package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class CapacityAlreadyLoadedException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.CAPACITY_ALREADY_LOADED;

    public CapacityAlreadyLoadedException() {
        super(API_STATUS);
    }

    public CapacityAlreadyLoadedException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public CapacityAlreadyLoadedException(String message) {
        super(message, API_STATUS);
    }

    public CapacityAlreadyLoadedException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}