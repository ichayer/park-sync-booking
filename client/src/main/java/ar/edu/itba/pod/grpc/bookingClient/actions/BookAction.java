package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.ReservationResponse;
import ar.edu.itba.pod.grpc.ReservationStatus;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.SlotValidator;
import ar.edu.itba.pod.grpc.interfaces.Action;

public class BookAction implements Action {
    private String attractionName;
    private int dayOfYear;
    private String slot;
    private String visitorId;
    private boolean success;
    private String responseMessage;
    ReservationStatus status;

    @Override
    public Action execute(Arguments arguments) {
        if(arguments.getDayOfYear() == null || arguments.getVisitorId() == null || arguments.getRideName() == null || arguments.getBookingSlot() == null){
            throw new IllegalClientArgumentException("The book action requires the day of year, the visitorId, the ride and the booking slot, " +
                    "use -Dday=dayOfYear -ride=rideName -Dvisitor=visitorId -Dslot=bookingSlot ");
        }

        BookingServiceGrpc.BookingServiceBlockingStub stub = BookingServiceGrpc.newBlockingStub(arguments.getChannel());

        attractionName = arguments.getRideName();
        dayOfYear = arguments.getDayOfYear();
        slot = arguments.getBookingSlot();
        visitorId = arguments.getVisitorId();

        if(!SlotValidator.isValidSlot(slot)){
            throw new IllegalClientArgumentException("The hour format is not valid the format is: -Dslot=HH:MM");
        }

        BookingRequest request = BookingRequest.newBuilder()
                .setAttractionName(attractionName)
                .setDayOfYear(dayOfYear)
                .setSlot(slot)
                .setVisitorId(visitorId)
                .build();

//        ReservationResponse response = stub.reserveAttraction(request);
//
//        success = response.getSuccess();
//        responseMessage = response.getMessage();
//        status = response.getStatus();

        return this;
    }

    @Override
    public void showResults() {
        if(!success){
            System.out.println("There was a problem while trying yo make the booking: " + responseMessage);
        }

        System.out.printf("The reservation for %s at %s on the day %d is %s%n", attractionName, slot, dayOfYear, status.name());
    }
}
