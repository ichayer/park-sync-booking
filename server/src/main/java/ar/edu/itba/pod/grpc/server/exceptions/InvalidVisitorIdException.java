package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class InvalidVisitorIdException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.INVALID_VISITOR_ID;

    public InvalidVisitorIdException() {
        super(API_STATUS);
    }

    public InvalidVisitorIdException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public InvalidVisitorIdException(String message) {
        super(message, API_STATUS);
    }

    public InvalidVisitorIdException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}