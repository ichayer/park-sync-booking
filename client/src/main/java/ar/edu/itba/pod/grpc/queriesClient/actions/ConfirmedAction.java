package ar.edu.itba.pod.grpc.queriesClient.actions;

import ar.edu.itba.pod.grpc.ConfirmedReservation;
import ar.edu.itba.pod.grpc.ConfirmedReservationsResponse;
import ar.edu.itba.pod.grpc.DayOfYearRequest;
import ar.edu.itba.pod.grpc.QueryServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

public class ConfirmedAction extends QueriesAction {
    List<ConfirmedReservation> reservations;
    private static final Logger logger = LoggerFactory.getLogger(ConfirmedAction.class);


    @Override
    protected void sendServerMessage(DayOfYearRequest request, QueryServiceGrpc.QueryServiceBlockingStub stub) {
        logger.info("Sending confirmed actions query request {}", request);
        ConfirmedReservationsResponse response = stub.getConfirmedReservations(request);

        reservations = response.getConfirmedReservationList();
    }

    @Override
    protected void writeToFile(PrintWriter writer) {
        writer.printf("%-7s | %-25s | %-7s%n", "Slot", "Visitor", "Attraction");

        for (ConfirmedReservation reservation : reservations) {
            String line = String.format("%-7s | %-25s | %-7s%n",
                    reservation.getSlot(),
                    reservation.getVisitorId(),
                    reservation.getAttractionName());
            writer.println(line);
        }
    }
}
