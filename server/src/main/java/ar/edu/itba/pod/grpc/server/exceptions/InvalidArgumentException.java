package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;
import io.grpc.Status;

public class InvalidArgumentException extends ServerException{

    public InvalidArgumentException(ApiStatus apiStatus) {
        super(Status.INVALID_ARGUMENT, apiStatus);
    }

    public InvalidArgumentException(ApiStatus apiStatus, Throwable cause) {
        super(Status.INVALID_ARGUMENT, apiStatus, cause);
    }
}
