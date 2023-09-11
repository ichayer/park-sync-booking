package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class CheckAvailabilityInvalidArgumentException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.CHECK_AVAILABILITY_INVALID_ARGUMENT_EXCEPTION;

    public CheckAvailabilityInvalidArgumentException() {
        super(API_STATUS);
    }

    public CheckAvailabilityInvalidArgumentException(ApiStatus apiStatus, Throwable cause) {
        super(apiStatus, cause);
    }

    public CheckAvailabilityInvalidArgumentException(String message, ApiStatus apiStatus) {
        super(message, apiStatus);
    }

    public CheckAvailabilityInvalidArgumentException(String message, Throwable cause, ApiStatus apiStatus) {
        super(message, cause, apiStatus);
    }
}
