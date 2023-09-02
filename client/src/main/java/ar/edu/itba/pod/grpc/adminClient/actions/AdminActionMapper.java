package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.helpers.ActionMapper;

import java.util.Map;

public class AdminActionMapper extends ActionMapper {

    public AdminActionMapper(){
        super(Map.of(
                "RIDES", RidesAction::new,
                "TICKETS", TicketsAction::new,
                "SLOTS", SlotsAction::new));
    }
}
