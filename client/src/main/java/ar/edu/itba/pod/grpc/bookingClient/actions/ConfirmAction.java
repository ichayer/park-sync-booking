package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmAction extends BoookingAction {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmAction.class);

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        logger.info("Sending confirm request {}", bookingRequest);
        stub.confirmReservation(bookingRequest);
    }

    @Override
    public void showResults() {
        System.out.printf("The reservation for %s at %s on the day %d was successfully confirmed%n", attractionName, slot, dayOfYear);
    }

}
