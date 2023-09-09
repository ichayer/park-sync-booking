package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;
import io.grpc.Status;

public class ServerException extends RuntimeException{

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    io.grpc.Status getStatus() {
        return Status.UNKNOWN.withDescription(ApiStatus.UNKNOWN.getMessageCode());
    }
}
