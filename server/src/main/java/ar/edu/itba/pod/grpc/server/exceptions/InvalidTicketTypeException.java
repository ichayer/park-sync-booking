package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class InvalidTicketTypeException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.INVALID_TICKET_TYPE;

    public InvalidTicketTypeException() {
        super(API_STATUS);
    }

    public InvalidTicketTypeException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public InvalidTicketTypeException(String message) {
        super(message, API_STATUS);
    }

    public InvalidTicketTypeException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}

