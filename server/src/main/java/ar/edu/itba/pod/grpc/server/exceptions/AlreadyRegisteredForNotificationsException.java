package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class AlreadyRegisteredForNotificationsException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.ALREADY_REGISTERED_FOR_NOTIFICATIONS;

    public AlreadyRegisteredForNotificationsException() {
        super(API_STATUS);
    }

    public AlreadyRegisteredForNotificationsException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public AlreadyRegisteredForNotificationsException(String message) {
        super(message, API_STATUS);
    }

    public AlreadyRegisteredForNotificationsException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}