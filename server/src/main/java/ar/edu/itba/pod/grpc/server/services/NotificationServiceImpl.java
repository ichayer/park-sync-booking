package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.AttractionNotificationServiceGrpc;
import ar.edu.itba.pod.grpc.Notification;
import ar.edu.itba.pod.grpc.NotificationRequest;
import ar.edu.itba.pod.grpc.NotificationType;
import ar.edu.itba.pod.grpc.server.handlers.AttractionHandler;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.ConfirmedReservation;
import ar.edu.itba.pod.grpc.server.models.Reservation;
import ar.edu.itba.pod.grpc.server.notifications.NotificationRouter;
import ar.edu.itba.pod.grpc.server.notifications.NotificationStreamObserver;
import ar.edu.itba.pod.grpc.server.utils.ParseUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
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

        Attraction attraction = attractionHandler.getAttraction(attractionName);

        NotificationStreamObserverImpl notificationStream = new NotificationStreamObserverImpl(responseObserver);
        notificationRouter.subscribe(notificationStream, attraction, visitorId, dayOfYear);
    }

    @Override
    public void unfollow(NotificationRequest request, StreamObserver<Empty> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getRideName());
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());

        Attraction attraction = attractionHandler.getAttraction(attractionName);

        notificationRouter.unsubscribe(attraction, visitorId, dayOfYear);
    }

    private static class NotificationStreamObserverImpl implements NotificationStreamObserver {
        private final StreamObserver<Notification> streamObserver;
        private boolean completed = false;

        public NotificationStreamObserverImpl(StreamObserver<Notification> streamObserver) {
            this.streamObserver = streamObserver;
        }

        @Override
        public synchronized void onComplete() {
            streamObserver.onCompleted();
            completed = true;
        }

        @Override
        public synchronized void onSlotCapacitySet(Attraction attraction, int dayOfYear, int slotCapacity) {
            if (completed)
                return;

            Notification notification = Notification.newBuilder()
                    .setType(NotificationType.NOTIFICATION_TYPE_BOOKING_SLOT_CAPACITY_SET)
                    .setSlotCapacity(slotCapacity)
                    .build();

            streamObserver.onNext(notification);
        }

        @Override
        public synchronized void onCreated(Reservation reservation, LocalTime slotTime, boolean isConfirmed) {
            if (completed)
                return;

            Notification notification = Notification.newBuilder()
                    .setType(isConfirmed ? NotificationType.NOTIFICATION_TYPE_BOOKING_CREATED_CONFIRMED : NotificationType.NOTIFICATION_TYPE_BOOKING_CREATED_PENDING)
                    .setSlotTime(ParseUtils.formatTime(slotTime))
                    .build();

            streamObserver.onNext(notification);
        }

        @Override
        public synchronized void onConfirmed(ConfirmedReservation reservation) {
            if (completed)
                return;

            Notification notification = Notification.newBuilder()
                    .setType(NotificationType.NOTIFICATION_TYPE_BOOKING_CONFIRMED)
                    .setSlotTime(ParseUtils.formatTime(reservation.getSlotTime()))
                    .build();

            streamObserver.onNext(notification);
        }

        @Override
        public synchronized void onRelocated(Reservation reservation, LocalTime prevSlotTime, LocalTime newSlotTime) {
            if (completed)
                return;

            Notification notification = Notification.newBuilder()
                    .setType(NotificationType.NOTIFICATION_TYPE_BOOKING_RELOCATED)
                    .setSlotTime(ParseUtils.formatTime(prevSlotTime))
                    .setRelocatedTo(ParseUtils.formatTime(newSlotTime))
                    .build();

            streamObserver.onNext(notification);
        }

        @Override
        public synchronized void onCancelled(Reservation reservation, LocalTime slotTime) {
            if (completed)
                return;

            Notification notification = Notification.newBuilder()
                    .setType(NotificationType.NOTIFICATION_TYPE_BOOKING_CANCELLED)
                    .setSlotTime(ParseUtils.formatTime(slotTime))
                    .build();

            streamObserver.onNext(notification);
        }
    }
}
