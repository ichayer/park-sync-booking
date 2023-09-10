package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class EmptyAttractionException extends ServerException{

    private static final ApiStatus API_STATUS = ApiStatus.EMPTY_ATTRACTION;

    public EmptyAttractionException() {
        super(API_STATUS);
    }

    public EmptyAttractionException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public EmptyAttractionException(String message) {
        super(message, API_STATUS);
    }

    public EmptyAttractionException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}
