package ar.edu.itba.pod.grpc.queriesClient.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.bookingClient.actions.CancelAction;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.PrintWriter;
import java.util.List;


public class CapacityAction extends QueriesAction {

    private SuggestedCapacityStatus status;
    private List<SuggestedCapacity> suggestedCapacities;
    private static final Logger logger = LoggerFactory.getLogger(CapacityAction.class);



    @Override
    protected void sendServerMessage(DayOfYearRequest request, QueryServiceGrpc.QueryServiceBlockingStub stub) {
        logger.info("Sending capacity request {}", request);

        SuggestedCapacitiesResponse response = stub.getSuggestedCapacities(request);

        status = response.getStatus();
        suggestedCapacities = response.getSuggestedCapacityList();
    }

    @Override
    protected void writeToFile(PrintWriter writer){
        if (!status.equals(SuggestedCapacityStatus.SUGGESTED_CAPACITY_STATUS_SUCCESS)) {
            String response = switch (status) {
                case SUGGESTED_CAPACITY_STATUS_INVALID_DAY -> "Invalid day.";
                default -> "Unknown status.";
            };
            throw new ServerErrorReceived("There was a problem while trying to retrieve the suggested capacity: " + response);
        }

        writer.printf("%-7s | %-8s | %-7s%n", "Slot", "Capacity", "Attraction");
        for (SuggestedCapacity capacity : suggestedCapacities) {
            writer.printf("%-7s | %8d | %-7s%n", capacity.getSlotWithMaxReservations(), capacity.getMaxPendingReservations(), capacity.getAttractionName());
        }
    }
}
