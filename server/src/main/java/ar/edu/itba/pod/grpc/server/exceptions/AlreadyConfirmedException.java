package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class AlreadyConfirmedException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.ALREADY_CONFIRMED;

    public AlreadyConfirmedException() {
        super(API_STATUS);
    }

    public AlreadyConfirmedException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public AlreadyConfirmedException(String message) {
        super(message, API_STATUS);
    }

    public AlreadyConfirmedException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}
