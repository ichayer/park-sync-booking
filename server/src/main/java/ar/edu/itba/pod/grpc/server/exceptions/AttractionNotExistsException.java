package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class AttractionNotExistsException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.ATTRACTION_NOT_EXISTS;

    public AttractionNotExistsException() {
        super(API_STATUS);
    }

    public AttractionNotExistsException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public AttractionNotExistsException(String message) {
        super(message, API_STATUS);
    }

    public AttractionNotExistsException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}
