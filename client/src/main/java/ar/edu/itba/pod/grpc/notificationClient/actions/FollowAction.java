package ar.edu.itba.pod.grpc.notificationClient.actions;

import ar.edu.itba.pod.grpc.AttractionNotificationServiceGrpc;
import ar.edu.itba.pod.grpc.Notification;
import ar.edu.itba.pod.grpc.NotificationRequest;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(FollowAction.class);
    private Arguments arguments;

    @Override
    public Action execute(Arguments arguments) {
        if (arguments.getAttractionName() == null || arguments.getVisitorId() == null || arguments.getDayOfYear() == null) {
            throw new IllegalClientArgumentException("The action follow requires the attraction name, the visitorId and the day of year, " +
                    "use -Dday=dayOfYear -Dride=rideName -Dvisitor=visitorId\n");
        }

        this.arguments = arguments;

        NotificationRequest request = NotificationRequest.newBuilder()
                .setRideName(arguments.getAttractionName())
                .setVisitorId(arguments.getVisitorId())
                .setDayOfYear(arguments.getDayOfYear())
                .build();

        logger.info("Sending follow request {}", request);


        AttractionNotificationServiceGrpc.AttractionNotificationServiceStub asyncStub =
                AttractionNotificationServiceGrpc.newStub(arguments.getChannel());;

        asyncStub.follow(request, new StreamObserver<>() {
            @Override
            public void onNext(Notification response) {
                String msg = switch (response.getType()) {
                    case NOTIFICATION_TYPE_BOOKING_SLOT_CAPACITY_SET ->
                            String.format("%s announced slot capacity for the day %d: %d places.",
                                    arguments.getAttractionName(),
                                    arguments.getDayOfYear(),
                                    response.getSlotCapacity());
                    case NOTIFICATION_TYPE_BOOKING_CREATED_PENDING ->
                            String.format("The reservation for %s at %s on the day %d is PENDING",
                                    arguments.getAttractionName(),
                                    response.getSlotTime(),
                                    arguments.getDayOfYear());
                    case NOTIFICATION_TYPE_BOOKING_CREATED_CONFIRMED, NOTIFICATION_TYPE_BOOKING_CONFIRMED ->
                            String.format("The reservation for %s at %s on the day %d is CONFIRMED",
                            arguments.getAttractionName(),
                            response.getSlotTime(),
                            arguments.getDayOfYear());
                    case NOTIFICATION_TYPE_BOOKING_CANCELLED ->
                            String.format("The reservation for %s at %s on the day %d is CANCELLED",
                                    arguments.getAttractionName(),
                                    response.getSlotTime(),
                                    arguments.getDayOfYear());
                    case NOTIFICATION_TYPE_BOOKING_RELOCATED ->
                            String.format("The reservation for %s at %s on the day %d was moved to %s and is PENDING",
                                    arguments.getAttractionName(),
                                    response.getSlotTime(),
                                    arguments.getDayOfYear(),
                                    response.getRelocatedTo());
                    case NOTIFICATION_TYPE_UNKNOWN, UNRECOGNIZED -> "Unrecognized command";
                };

                System.out.println(msg);
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
