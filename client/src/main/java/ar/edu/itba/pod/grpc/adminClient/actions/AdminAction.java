package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.adminClient.AdminArguments;
import ar.edu.itba.pod.grpc.interfaces.Action;

public abstract class AdminAction implements Action {
    protected final AdminArguments arguments;

    public AdminAction(AdminArguments arguments){
        this.arguments = arguments;
    }
}
