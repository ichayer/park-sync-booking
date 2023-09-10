package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class UnknownException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.UNKNOWN;

    public UnknownException() {
        super(API_STATUS);
    }

    public UnknownException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public UnknownException(String message) {
        super(message, API_STATUS);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}
