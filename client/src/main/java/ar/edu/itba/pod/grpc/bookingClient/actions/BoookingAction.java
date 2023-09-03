package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.SlotValidator;
import ar.edu.itba.pod.grpc.interfaces.Action;

public abstract class BoookingAction implements Action {

    protected String attractionName;
    protected int dayOfYear;
    protected String slot;
    protected String visitorId;

    protected boolean success;
    protected String responseMessage;

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

        sendServerMessage(request, stub);

        return this;
    }

    protected abstract void sendServerMessage(BookingRequest bookingRequest, BookingServiceGrpc.BookingServiceBlockingStub stub);
}
