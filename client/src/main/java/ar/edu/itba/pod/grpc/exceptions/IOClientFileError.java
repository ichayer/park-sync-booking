package ar.edu.itba.pod.grpc.exceptions;

public class IOClientFileError extends RuntimeException{
    public IOClientFileError(String message) {
        super(message);
    }

    public IOClientFileError(String message, Throwable cause) {
        super(message, cause);
    }
}
