package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AddCapacityStatus;
import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.AddCapacityRequest;
import ar.edu.itba.pod.grpc.AddCapacityResponse;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class SlotsAction implements Action {
    private int confirmedBookings;
    private int relocatedBookings;
    private int cancelledBookings;
    private AddCapacityStatus status;
    private Arguments arguments;

    private static final Logger logger = LoggerFactory.getLogger(SlotsAction.class);

    @Override
    public Action execute(Arguments arguments) {
        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());
        if (arguments.getDayOfYear() == null || arguments.getAttractionName() == null || arguments.getCapacity() == null) {
            throw new IllegalClientArgumentException("The slots action must be provided a day, a name and a capacity " +
                    "with the arguments -Dday=day -Dride=rideName -Dcapacity=capacity");
        }
        this.arguments = arguments;

        AddCapacityRequest request = AddCapacityRequest.newBuilder()
                .setAttractionName(arguments.getAttractionName())
                .setDayOfYear(arguments.getDayOfYear())
                .setCapacity(arguments.getCapacity())
                .build();
        logger.info("Sending slot request {}", request);

        AddCapacityResponse response = stub.addCapacity(request);

        confirmedBookings = response.getConfirmedBookings();
        relocatedBookings = response.getRelocatedBookings();
        cancelledBookings = response.getCancelledBookings();
        status = response.getStatus();

        return this;
    }

    @Override
    public void showResults() {
        if (status.equals(AddCapacityStatus.ADD_CAPACITY_STATUS_SUCCESS)) {
            System.out.printf("Loaded capacity of %d for %s on day %d%n", arguments.getCapacity(), arguments.getAttractionName(), arguments.getDayOfYear());
            System.out.printf("%d bookings confirmed without changes%n", confirmedBookings);
            System.out.printf("%d bookings relocated%n", relocatedBookings);
            System.out.printf("%d bookings cancelled%n", cancelledBookings);
        } else {
            String response = switch (status) {
                case ADD_CAPACITY_STATUS_NOT_EXISTS -> "Attraction does not exist.";
                case ADD_CAPACITY_STATUS_INVALID_DAY -> "Invalid day.";
                case ADD_CAPACITY_STATUS_NEGATIVE_CAPACITY -> "Capacity cannot be negative.";
                case ADD_CAPACITY_STATUS_CAPACITY_ALREADY_LOADED -> "Capacity has already been loaded.";
                default -> "Unknown status.";
            };
            System.out.println("There was a problem while trying to load the capacity: " + response);
        }
    }
}
