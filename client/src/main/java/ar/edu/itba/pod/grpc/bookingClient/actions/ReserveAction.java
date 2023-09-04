package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.ReservationStatus;
import ar.edu.itba.pod.grpc.ReservationResponse;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;

import static ar.edu.itba.pod.grpc.ReservationStatus.BOOKING_STATUS_SUCCESS;

public class ReserveAction extends BoookingAction {

    ReservationStatus status;

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        ReservationResponse response = stub.reserveAttraction(bookingRequest);
        status = response.getStatus();
    }

    @Override
    public void showResults() {
        if(status.equals(BOOKING_STATUS_SUCCESS)){
            System.out.printf("The reservation for %s at %s on the day %d is %s%n", attractionName, slot, dayOfYear, status.name());
        }
        else {
            String response = switch (status) {
                case BOOKING_STATUS_ALREADY_EXISTS -> "Reservation already exists.";
                case BOOKING_STATUS_ATTRACTION_NOT_FOUND -> "Attraction not found.";
                case BOOKING_STATUS_INVALID_DAY -> "Invalid day.";
                case BOOKING_STATUS_INVALID_SLOT -> "Invalid slot.";
                case BOOKING_STATUS_MISSING_PASS -> "Client does not have a valid pass.";
                case BOOKING_STATUS_NO_CAPACITY -> "No capacity available.";
                default -> "Unknown status.";
            };
            throw new ServerErrorReceived("There was a problem while trying to make the booking: " + response);
        }
    }
}
