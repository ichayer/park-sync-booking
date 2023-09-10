package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;
import com.google.rpc.context.AttributeContext;
import io.grpc.Status;

public class ServerException extends RuntimeException{

    private final Status status;
    private final ApiStatus apiStatus;

    public ServerException(Status status, ApiStatus apiStatus) {
        this.apiStatus = apiStatus;
        this.status = status;
    }

    public ServerException(Status status, ApiStatus apiStatus, Throwable cause) {
        super(cause);
        this.apiStatus = apiStatus;
        this.status = status;
    }

    io.grpc.Status getStatus() {
        return status.withDescription(apiStatus.getMessageCode());
    }
}
