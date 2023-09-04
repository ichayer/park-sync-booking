package ar.edu.itba.pod.grpc.queriesClient;

import ar.edu.itba.pod.grpc.helpers.ActionMapperImpl;
import ar.edu.itba.pod.grpc.queriesClient.actions.CapacityAction;
import ar.edu.itba.pod.grpc.queriesClient.actions.ConfirmedAction;

import java.util.Map;

public class QueriesActionMapper extends ActionMapperImpl {

    public QueriesActionMapper() {
        super(Map.of(
                "CAPACITY", CapacityAction::new,
                "CONFIRMED", ConfirmedAction::new));
    }
}
