package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.interfaces.Action;
import ar.edu.itba.pod.grpc.interfaces.ActionMapper;

import java.util.Map;
import java.util.function.Supplier;

public abstract class ActionMapperImpl implements ActionMapper {

    private final Map<String, Supplier<Action>> actionMapper;

    protected ActionMapperImpl(Map<String, Supplier<Action>> actionMapper) {
        this.actionMapper = actionMapper;
    }

    @Override
    public Action getAction(String actionName) {
        return actionMapper.getOrDefault(actionName, ActionMapperImpl::InvalidAction).get();
    }

    private static Action InvalidAction(){
        throw new IllegalClientArgumentException("The provided action is not valid");
    }
}
