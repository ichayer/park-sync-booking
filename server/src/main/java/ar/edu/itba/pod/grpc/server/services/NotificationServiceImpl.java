package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.AttractionNotificationServiceGrpc;
import ar.edu.itba.pod.grpc.Notification;
import ar.edu.itba.pod.grpc.NotificationRequest;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.notifications.NotificationRouter;
import ar.edu.itba.pod.grpc.server.utils.ParseUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class NotificationServiceImpl extends AttractionNotificationServiceGrpc.AttractionNotificationServiceImplBase {
    private final AttractionHandler attractionHandler;
    private final NotificationRouter notificationRouter;

    public NotificationServiceImpl(AttractionHandler attractionHandler, NotificationRouter notificationRouter) {
        this.attractionHandler = attractionHandler;
        this.notificationRouter = notificationRouter;
    }
    @Override
    public void follow(NotificationRequest request, StreamObserver<Notification> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getRideName());
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());

        super.follow(request, responseObserver);
    }

    @Override
    public void unfollow(NotificationRequest request, StreamObserver<Empty> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getRideName());
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());

        super.unfollow(request, responseObserver);
    }
}
