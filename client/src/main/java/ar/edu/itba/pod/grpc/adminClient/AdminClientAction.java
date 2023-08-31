package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;

public enum AdminClientAction {
    RIDES,
    TICKETS,
    SLOTS;

    public static AdminClientAction getAction(String arg) {
        for (AdminClientAction actions : AdminClientAction.values()) {
            if (actions.name().equals(arg)) {
                return actions;
            }
        }
        throw new RuntimeException(arg + "is not a valid Action");
    }
}
