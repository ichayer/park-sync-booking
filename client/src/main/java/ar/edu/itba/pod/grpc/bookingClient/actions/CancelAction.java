package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.CancellationResponse;
import ar.edu.itba.pod.grpc.CancellationStatus;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;

public class CancelAction extends BoookingAction {

    private CancellationStatus status;

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        CancellationResponse response = stub.cancelReservation(bookingRequest);
        status = response.getStatus();
    }

    @Override
    public void showResults() {
        if(status.equals(CancellationStatus.CANCELLATION_STATUS_SUCCESS)){
            System.out.printf("The reservation for %s at %s on the day %d was successfully canceled%n", attractionName, slot, dayOfYear);
        }
        else {
            String response = switch (status) {
                case CANCELLATION_STATUS_RESERVATION_NOT_FOUND -> "Reservation not found.";
                case CANCELLATION_STATUS_ATTRACTION_NOT_FOUND -> "Attraction not found.";
                case CANCELLATION_STATUS_INVALID_DAY -> "Invalid day.";
                case CANCELLATION_STATUS_INVALID_SLOT -> "Invalid slot.";
                case CANCELLATION_STATUS_MISSING_PASS -> "Client does not have a valid pass.";
                default -> "Unknown status.";
            };

            throw new ServerErrorReceived("There was a problem while trying to make the cancellation: " + response);
        }
    }
}
