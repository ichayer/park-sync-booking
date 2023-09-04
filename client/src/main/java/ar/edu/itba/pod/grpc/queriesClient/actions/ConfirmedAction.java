package ar.edu.itba.pod.grpc.queriesClient.actions;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

public class ConfirmedAction extends QueriesAction{

    ConfirmedCapacityStatus status;
    List<ConfirmedReservation> reservations;

    @Override
    protected void sendServerMessage(DayOfYearRequest request, QueryServiceGrpc.QueryServiceBlockingStub stub) {
        ConfirmedReservationsResponse response = stub.getConfirmedReservations(request);

        status = response.getStatus();
        reservations = response.getConfirmedReservationList();
    }

    @Override
    protected void writeToFile(PrintWriter writer){

        if (!status.equals(ConfirmedCapacityStatus.CONFIRMED_RESERVATION_STATUS_SUCCESS)){
            String response = switch (status) {
                case CONFIRMED_RESERVATION_STATUS_INVALID_DAY -> "Invalid day.";
                default -> "Unknown status.";
            };
            throw new ServerErrorReceived("There was a problem while trying to retrieve the confirmed capacity: " + response);
        }

        writer.printf("%-7s | %-25s | %-7s%n", "Slot", "Visitor", "Attraction");

        reservations.sort(Comparator
                .comparing(ConfirmedReservation::getAttractionName)
                .thenComparing(ConfirmedReservation::getSlot)
                .thenComparing(ConfirmedReservation::getVisitorId));

        for (ConfirmedReservation reservation : reservations) {
            String line = String.format("%-7s | %-25s | %-7s%n",
                    reservation.getSlot(),
                    reservation.getVisitorId(),
                    reservation.getAttractionName());
            writer.println(line);
        }

    }
}
