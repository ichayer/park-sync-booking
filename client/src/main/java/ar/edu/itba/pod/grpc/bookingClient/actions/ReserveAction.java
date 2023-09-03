package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.ReservationStatus;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.SlotValidator;
import ar.edu.itba.pod.grpc.interfaces.Action;

public class ReserveAction extends BoookingAction {
    ReservationStatus status;

    @Override
    protected void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub) {

//        ReservationResponse response = stub.reserveAttraction(request);
//
//        success = response.getSuccess();
//        responseMessage = response.getMessage();
//        status = response.getStatus();
    }

    @Override
    public void showResults() {
        if(!success){
            System.out.println("There was a problem while trying to make the booking: " + responseMessage);
        }

        System.out.printf("The reservation for %s at %s on the day %d is %s%n", attractionName, slot, dayOfYear, status.name());
    }
}
