package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class AttractionNotFoundException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.ATTRACTION_NOT_FOUND;

    public AttractionNotFoundException() {
        super(API_STATUS);
    }

    public AttractionNotFoundException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public AttractionNotFoundException(String message) {
        super(message, API_STATUS);
    }

    public AttractionNotFoundException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}
