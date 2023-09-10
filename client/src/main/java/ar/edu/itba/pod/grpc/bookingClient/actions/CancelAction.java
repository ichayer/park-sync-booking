package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelAction extends BoookingAction {
    private static final Logger logger = LoggerFactory.getLogger(CancelAction.class);

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        logger.info("Sending cancel request {}", bookingRequest);
        stub.cancelReservation(bookingRequest);
    }

    @Override
    public void showResults() {
        System.out.printf("The reservation for %s at %s on the day %d was successfully canceled%n", attractionName, slot, dayOfYear);
    }
}
