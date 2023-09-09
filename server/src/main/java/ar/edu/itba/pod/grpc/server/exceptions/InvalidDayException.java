package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class InvalidDayException extends ServerException{
    public InvalidDayException(String message) {
        super(message);
    }

    public InvalidDayException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public io.grpc.Status getStatus() {
        return io.grpc.Status.INVALID_ARGUMENT.withDescription(ApiStatus.INVALID_DAY.getMessageCode());
    }
}
