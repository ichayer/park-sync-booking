package ar.edu.itba.pod.grpc.bookingClient;

import ar.edu.itba.pod.grpc.bookingClient.actions.*;
import ar.edu.itba.pod.grpc.helpers.ActionMapper;
import ar.edu.itba.pod.grpc.interfaces.Action;

import java.util.Map;
import java.util.function.Supplier;

public class BookingActionMapper extends ActionMapper {
    protected BookingActionMapper() {
        super(Map.of(
                "ATTRACTIONS", AttractionsAction::new,
                "AVAILABILITY", AvailabilityAction::new,
                "BOOK", BookAction::new,
                "CONFIRM", ConfirmAction::new,
                "CANCEL", CancelAction::new));
    }
}
