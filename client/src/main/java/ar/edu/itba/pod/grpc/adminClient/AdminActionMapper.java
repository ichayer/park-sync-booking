package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.adminClient.actions.RidesAction;
import ar.edu.itba.pod.grpc.adminClient.actions.SlotsAction;
import ar.edu.itba.pod.grpc.adminClient.actions.TicketsAction;
import ar.edu.itba.pod.grpc.helpers.ActionMapperImpl;

import java.util.Map;

public class AdminActionMapper extends ActionMapperImpl {

    public AdminActionMapper() {
        super(Map.of(
                "RIDES", RidesAction::new,
                "TICKETS", TicketsAction::new,
                "SLOTS", SlotsAction::new));
    }
}
