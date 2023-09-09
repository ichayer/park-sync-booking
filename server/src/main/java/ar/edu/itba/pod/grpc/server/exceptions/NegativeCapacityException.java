package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class NegativeCapacityException extends ServerException{
    public NegativeCapacityException(String message) {
        super(message);
    }

    public NegativeCapacityException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public io.grpc.Status getStatus() {
        return io.grpc.Status.INVALID_ARGUMENT.withDescription(ApiStatus.NEGATIVE_CAPACITY.getMessageCode());
    }
}
