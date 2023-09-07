package ar.edu.itba.pod.grpc.notificationClient;

import ar.edu.itba.pod.grpc.notificationClient.actions.FollowAction;
import ar.edu.itba.pod.grpc.notificationClient.actions.UnfollowAction;
import ar.edu.itba.pod.grpc.helpers.ActionMapperImpl;

import java.util.Map;

public class NotificationActionMapper extends ActionMapperImpl {

    public NotificationActionMapper(){
        super(Map.of(
                "FOLLOW", FollowAction::new,
                "UNFOLLOW", UnfollowAction::new));
    }
}