package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.interfaces.Action;

import java.util.Map;
import java.util.function.Supplier;

public abstract class ActionMapper {

    private final Map<String, Supplier<Action>> actionMapper;

    protected ActionMapper(Map<String, Supplier<Action>> actionMapper) {
        this.actionMapper = actionMapper;
    }

    public Action getAction(String actionName) {
        return actionMapper.getOrDefault(actionName, ActionMapper::InvalidAction).get();
    }

    private static Action InvalidAction(){
        throw new IllegalClientArgumentException("The provided action is not valid");
    }
}
