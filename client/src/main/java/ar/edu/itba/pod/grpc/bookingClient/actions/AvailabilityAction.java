package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.AvailabilityRequest;
import ar.edu.itba.pod.grpc.AvailabilityResponse;
import ar.edu.itba.pod.grpc.AvailabilitySlot;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AvailabilityAction implements Action {

    List<AvailabilitySlot> slots;
    private static final Logger logger = LoggerFactory.getLogger(AvailabilityAction.class);

    @Override
    public Action execute(Arguments arguments) {
        AvailabilityRequest.Builder requestBuilder = AvailabilityRequest.newBuilder();
        if(arguments.getDayOfYear() == null){
            throw new IllegalClientArgumentException("Invalid arguments for the availability action.");
        }
        requestBuilder.setDayOfYear(arguments.getDayOfYear());
        if (arguments.getAttractionName() == null && arguments.getBookingSlot() != null && arguments.getBookingSlotTo() != null) {
            requestBuilder.setSlotFrom(arguments.getBookingSlot());
            requestBuilder.setSlotTo(arguments.getBookingSlotTo());
        } else if (arguments.getAttractionName() != null && arguments.getBookingSlot() != null) {
            requestBuilder.setAttractionName(arguments.getAttractionName());
            requestBuilder.setSlotFrom(arguments.getBookingSlot());
            if (arguments.getBookingSlotTo() != null) {
                requestBuilder.setSlotTo(arguments.getBookingSlotTo());
            }
        } else {
            throw new IllegalClientArgumentException("Invalid arguments for the availability action.");
        }

        BookingServiceGrpc.BookingServiceBlockingStub client = BookingServiceGrpc.newBlockingStub(arguments.getChannel());

        AvailabilityRequest request = requestBuilder.build();
        logger.info("Sending availability request {}", request);

        AvailabilityResponse response = client.checkAttractionAvailability(request);

        slots = response.getSlotList();
        return this;
    }

    @Override
    public void showResults() {
        System.out.printf("%-7s | %9s | %9s | %9s | %-7s\n",
                "Slot", "Capacity", "Pending", "Confirmed", "Attraction");
        for (AvailabilitySlot slot : slots) {
            System.out.printf("%-7s | %9s | %9d | %9d | %-7s\n",
                    slot.getSlot(),
                    slot.getSlotCapacity()==-1 ?"X":String.valueOf(slot.getSlotCapacity()),
                    slot.getBookingsPending(),
                    slot.getBookingsConfirmed(),
                    slot.getAttractionName());
        }
    }
}
