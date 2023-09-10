package ar.edu.itba.pod.grpc.server.exceptions;

import ar.edu.itba.pod.grpc.errorHandling.ApiStatus;

public class ServerException extends RuntimeException{
    private final ApiStatus apiStatus;

    public ServerException(ApiStatus apiStatus) {
        this.apiStatus = apiStatus;
    }

    public ServerException(ApiStatus apiStatus, Throwable cause) {
        super(cause);
        this.apiStatus = apiStatus;
    }

    public ServerException(String message, ApiStatus apiStatus) {
        super(message);
        this.apiStatus = apiStatus;
    }

    public ServerException(String message, Throwable cause, ApiStatus apiStatus) {
        super(message, cause);
        this.apiStatus = apiStatus;
    }

    io.grpc.Status getStatus() {
        return apiStatus.getStatus().withDescription(apiStatus.getMessageCode());
    }
}
