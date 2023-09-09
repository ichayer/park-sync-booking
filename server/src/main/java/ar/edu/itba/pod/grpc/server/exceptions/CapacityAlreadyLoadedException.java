package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class CapacityAlreadyLoadedException extends ServerException{
    public CapacityAlreadyLoadedException(String message) {
        super(message);
    }

    public CapacityAlreadyLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public io.grpc.Status getStatus() {
        return io.grpc.Status.INVALID_ARGUMENT.withDescription(ApiStatus.CAPACITY_ALREADY_LOADED.getMessageCode());
    }
}
