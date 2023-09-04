package ar.edu.itba.pod.grpc.exceptions;

public class ServerErrorReceived extends RuntimeException{
    public ServerErrorReceived(String message) {
        super(message);
    }
}
