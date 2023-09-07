package ar.edu.itba.pod.grpc.notificationClient.actions;

import ar.edu.itba.pod.grpc.AttractionNotificationServiceGrpc;
import ar.edu.itba.pod.grpc.NotificationRequest;
import ar.edu.itba.pod.grpc.NotificationResponse;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import io.grpc.stub.StreamObserver;

public class FollowAction implements Action {
    @Override
    public Action execute(Arguments arguments) {
        if (arguments.getAttractionName() == null || arguments.getVisitorId() == null || arguments.getDayOfYear() == null) {
            throw new IllegalClientArgumentException("The action follow requires the attraction name, the visitorId and the day of year, " +
                    "use -Dday=dayOfYear -Dride=rideName -Dvisitor=visitorId\n");
        }

        NotificationRequest request = NotificationRequest.newBuilder()
                .setRideName(arguments.getAttractionName())
                .setVisitorId(arguments.getVisitorId())
                .setDayOfYear(arguments.getDayOfYear())
                .build();

        AttractionNotificationServiceGrpc.AttractionNotificationServiceStub asyncStub =
                AttractionNotificationServiceGrpc.newStub(arguments.getChannel());;

        asyncStub.follow(request, new StreamObserver<>() {
            @Override
            public void onNext(NotificationResponse response) {
                System.out.println(response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                // TODO: check if we want to do sth
            }
        });

        return this;
    }

    @Override
    public void showResults() {

    }
}
