package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.CancellationResponse;
import ar.edu.itba.pod.grpc.ConfirmationResponse;

public class CancelAction extends BoookingAction {

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {
        CancellationResponse response = stub.cancelReservation(bookingRequest);

        success = response.getSuccess();
        responseMessage = response.getMessage();
    }

    @Override
    public void showResults() {
        if(!success){
            System.out.println("There was a problem while trying to make the cancellation: " + responseMessage);
        }

        System.out.printf("The reservation for %s at %s on the day %d was successfully canceled%n", attractionName, slot, dayOfYear);
    }

}
