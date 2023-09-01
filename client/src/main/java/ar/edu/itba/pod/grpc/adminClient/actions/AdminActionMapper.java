package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.adminClient.AdminArguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import ar.edu.itba.pod.grpc.interfaces.ActionMapper;

import java.util.Map;
import java.util.function.Function;

public class AdminActionMapper implements ActionMapper {

    private Map<String, Function<AdminArguments, Action>> hashMap;
    private AdminArguments arguments;

    public AdminActionMapper(AdminArguments arguments){
        hashMap = Map.of("RIDES", (params) -> new RidesAction());
        this.arguments = arguments;
    }

    @Override
    public Action getAction(String actionName) {
        return hashMap.get(actionName).apply(arguments);
    }
}
