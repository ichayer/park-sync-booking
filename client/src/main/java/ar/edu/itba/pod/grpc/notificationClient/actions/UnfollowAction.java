package ar.edu.itba.pod.grpc.notificationClient.actions;

import ar.edu.itba.pod.grpc.AttractionNotificationServiceGrpc;
import ar.edu.itba.pod.grpc.NotificationResponse;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import ar.edu.itba.pod.grpc.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnfollowAction implements Action {

    private Arguments arguments;
    private String msg;

    private static final Logger logger = LoggerFactory.getLogger(UnfollowAction.class);


    @Override
    public Action execute(Arguments arguments) {

        if (arguments.getAttractionName() == null || arguments.getVisitorId() == null || arguments.getDayOfYear() == null) {
            throw new IllegalClientArgumentException("The action unfollow requires the attraction name, the visitorId and the day of year, " +
                    "use -Dday=dayOfYear -Dride=rideName -Dvisitor=visitorId\n");
        }

        this.arguments = arguments;

        NotificationRequest request = NotificationRequest.newBuilder()
                .setRideName(arguments.getAttractionName())
                .setVisitorId(arguments.getVisitorId())
                .setDayOfYear(arguments.getDayOfYear())
                .build();

        logger.info("Sending unfollow request {}", request);

        AttractionNotificationServiceGrpc.AttractionNotificationServiceBlockingStub stub =
                AttractionNotificationServiceGrpc.newBlockingStub(arguments.getChannel());
        NotificationResponse response = stub.unfollow(request);

        this.msg = response.getMessage();
        return this;
    }

    @Override
    public void showResults() {
        System.out.println(msg);
    }
}
