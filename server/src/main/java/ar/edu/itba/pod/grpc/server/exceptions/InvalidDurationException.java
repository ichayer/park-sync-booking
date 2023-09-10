package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class InvalidDurationException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.INVALID_DURATION;

    public InvalidDurationException() {
        super(API_STATUS);
    }

    public InvalidDurationException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public InvalidDurationException(String message) {
        super(message, API_STATUS);
    }

    public InvalidDurationException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}