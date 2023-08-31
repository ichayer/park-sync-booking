package ar.edu.itba.pod.grpc.exceptions;

public class IllegalClientArgumentException extends RuntimeException {

    public IllegalClientArgumentException(String message) {
        super(message);
    }

    public IllegalClientArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}

