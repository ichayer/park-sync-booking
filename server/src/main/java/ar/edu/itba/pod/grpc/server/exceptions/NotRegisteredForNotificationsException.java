package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class NotRegisteredForNotificationsException extends ServerException {

    private static final ApiStatus API_STATUS = ApiStatus.NOT_REGISTERED_FOR_NOTIFICATIONS;

    public NotRegisteredForNotificationsException() {
        super(API_STATUS);
    }

    public NotRegisteredForNotificationsException(Throwable cause) {
        super(API_STATUS, cause);
    }

    public NotRegisteredForNotificationsException(String message) {
        super(message, API_STATUS);
    }

    public NotRegisteredForNotificationsException(String message, Throwable cause) {
        super(message, cause, API_STATUS);
    }
}