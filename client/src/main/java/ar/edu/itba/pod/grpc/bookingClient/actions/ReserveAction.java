package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.BookingState;
import ar.edu.itba.pod.grpc.ReservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReserveAction extends BoookingAction {
    private static final Logger logger = LoggerFactory.getLogger(ReserveAction.class);

    BookingState bookingState;


    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        logger.info("Sending reserve request {}", bookingRequest);
        ReservationResponse response = stub.reserveAttraction(bookingRequest);
        bookingState = response.getState();
    }

    @Override
    public void showResults() {
        System.out.printf("The reservation for %s at %s on the day %d is %s%n", attractionName, slot, dayOfYear, bookingState.name());
    }

    private String bookingStateToString(BookingState bookingState){
        return switch (bookingState) {
            case RESERVATION_STATUS_UNKNOWN, UNRECOGNIZED -> "UNKNOWN";
            case RESERVATION_STATUS_PENDING -> "PENDING";
            case RESERVATION_STATUS_CONFIRMED -> "CONFIRMED";
        };
    }
}
