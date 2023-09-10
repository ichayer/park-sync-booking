package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;
import io.grpc.Status;

public class NotFoundException extends ServerException{
    public NotFoundException(ApiStatus apiStatus) {
        super(Status.NOT_FOUND, apiStatus);
    }

    public NotFoundException(ApiStatus apiStatus, Throwable cause) {
        super(Status.NOT_FOUND, apiStatus, cause);
    }

}
