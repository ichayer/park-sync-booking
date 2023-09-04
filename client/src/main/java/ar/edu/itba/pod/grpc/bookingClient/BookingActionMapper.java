package ar.edu.itba.pod.grpc.bookingClient;

import ar.edu.itba.pod.grpc.bookingClient.actions.*;
import ar.edu.itba.pod.grpc.helpers.ActionMapperImpl;

import java.util.Map;

public class BookingActionMapper extends ActionMapperImpl {
    public BookingActionMapper() {
        super(Map.of(
                "ATTRACTIONS", AttractionsAction::new,
                "AVAILABILITY", AvailabilityAction::new,
                "BOOK", ReserveAction::new,
                "CONFIRM", ConfirmAction::new,
                "CANCEL", CancelAction::new));
    }
}
