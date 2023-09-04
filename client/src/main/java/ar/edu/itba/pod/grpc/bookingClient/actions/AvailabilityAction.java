package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;

import java.util.List;

public class AvailabilityAction implements Action {

    CheckAvailabilityStatus status;
    List<AvailabilitySlot> slots;

    @Override
    public Action execute(Arguments arguments) {
        AvailabilityRequest.Builder requestBuilder = AvailabilityRequest.newBuilder();
        if(arguments.getAttractionName() == null && arguments.getBookingSlot() != null && arguments.getBookingSlotTo()!= null){
            requestBuilder.setSlotFrom(arguments.getBookingSlot());
            requestBuilder.setSlotTo(arguments.getBookingSlotTo());
        }
        else if(arguments.getAttractionName() != null && arguments.getBookingSlot() != null){
            requestBuilder.setAttractionName(arguments.getAttractionName());
            requestBuilder.setSlotFrom(arguments.getBookingSlot());
            if(arguments.getBookingSlotTo() != null){
                requestBuilder.setSlotTo(arguments.getBookingSlotTo());
            }
        }else {
            throw new IllegalClientArgumentException("Invalid arguments for the availability action.");

        }

        BookingServiceGrpc.BookingServiceBlockingStub client = BookingServiceGrpc.newBlockingStub(arguments.getChannel());

        AvailabilityResponse response = client.checkAttractionAvailability(requestBuilder.build());

        status = response.getStatus();
        slots = response.getSlotList();
        return this;
    }

    @Override
    public void showResults() {
        if (status == CheckAvailabilityStatus.CHECK_AVAILABILITY_SUCCESS) {
            System.out.printf("%-7s | %9s | %9s | %9s | %-7s\n",
                    "Slot", "Capacity", "Pending", "Confirmed", "Attraction");
            for (AvailabilitySlot slot : slots) {
                System.out.printf("%-7s | %9d | %9d | %9d | %-7s\n",
                        slot.getSlot(),
                        slot.getSlotCapacity(),
                        slot.getBookingsPending(),
                        slot.getBookingsConfirmed(),
                        slot.getAttractionName());
            }
        } else {
            throw new ServerErrorReceived("Error: Unable to retrieve availability information.");
        }
    }
}
