package ar.edu.itba.pod.grpc.queriesClient.actions;

import ar.edu.itba.pod.grpc.DayOfYearRequest;
import ar.edu.itba.pod.grpc.QueryServiceGrpc;
import ar.edu.itba.pod.grpc.SuggestedCapacitiesResponse;
import ar.edu.itba.pod.grpc.SuggestedCapacity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;


public class CapacityAction extends QueriesAction {
    private List<SuggestedCapacity> suggestedCapacities;
    private static final Logger logger = LoggerFactory.getLogger(CapacityAction.class);


    @Override
    protected void sendServerMessage(DayOfYearRequest request, QueryServiceGrpc.QueryServiceBlockingStub stub) {
        logger.info("Sending capacity request {}", request);

        SuggestedCapacitiesResponse response = stub.getSuggestedCapacities(request);

        suggestedCapacities = response.getSuggestedCapacityList();
    }

    @Override
    protected void writeToFile(PrintWriter writer) {
        writer.printf("%-7s | %-8s | %-7s%n", "Slot", "Capacity", "Attraction");
        for (SuggestedCapacity capacity : suggestedCapacities) {
            writer.printf("%-7s | %8d | %-7s%n", capacity.getSlotWithMaxReservations(), capacity.getMaxPendingReservations(), capacity.getAttractionName());
        }
    }
}
