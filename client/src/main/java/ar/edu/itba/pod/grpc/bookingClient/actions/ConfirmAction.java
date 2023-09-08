package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.ConfirmationResponse;
import ar.edu.itba.pod.grpc.ConfirmationStatus;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmAction extends BoookingAction {

    private ConfirmationStatus status;

    private static final Logger logger = LoggerFactory.getLogger(ConfirmAction.class);

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        logger.info("Sending confirm request {}", bookingRequest);
        ConfirmationResponse response = stub.confirmReservation(bookingRequest);
        status = response.getStatus();
    }

    @Override
    public void showResults() {
        if(status.equals(ConfirmationStatus.CONFIRMATION_STATUS_SUCCESS)){
            System.out.printf("The reservation for %s at %s on the day %d was successfully confirmed%n", attractionName, slot, dayOfYear);

        }
        else{
            String response = switch (status) {
                case CONFIRMATION_STATUS_NO_CAPACITY -> "No capacity available.";
                case CONFIRMATION_STATUS_ALREADY_CONFIRMED -> "Reservation already confirmed.";
                case CONFIRMATION_STATUS_RESERVATION_NOT_FOUND -> "Reservation not found.";
                case CONFIRMATION_STATUS_ATTRACTION_NOT_FOUND -> "Attraction not found.";
                case CONFIRMATION_STATUS_INVALID_DAY -> "Invalid day.";
                case CONFIRMATION_STATUS_INVALID_SLOT -> "Invalid slot.";
                case CONFIRMATION_STATUS_MISSING_PASS -> "Client does not have a valid pass.";
                default -> "Unknown status.";
            };
            throw new ServerErrorReceived("There was a problem while trying to make the confirmation: " + response);
        }
    }

}
