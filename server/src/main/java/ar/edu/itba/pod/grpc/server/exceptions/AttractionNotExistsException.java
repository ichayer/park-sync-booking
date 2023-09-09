package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class AttractionNotExistsException extends ServerException{
    public AttractionNotExistsException(String message) {
        super(message);
    }

    public AttractionNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public io.grpc.Status getStatus() {
        return io.grpc.Status.INVALID_ARGUMENT.withDescription(ApiStatus.ATTRACTION_NOT_EXISTS.getMessageCode());
    }
}
