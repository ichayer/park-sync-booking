package ar.edu.itba.pod.grpc.bookingClient;

import ar.edu.itba.pod.grpc.bookingClient.actions.*;
import ar.edu.itba.pod.grpc.helpers.ActionMapper;

import java.util.Map;

public class BookingActionMapper extends ActionMapper {
    protected BookingActionMapper() {
        super(Map.of(
                "ATTRACTIONS", AttractionsAction::new,
                "AVAILABILITY", AvailabilityAction::new,
                "BOOK", ReserveAction::new,
                "CONFIRM", ConfirmAction::new,
                "CANCEL", CancelAction::new));
    }
}
