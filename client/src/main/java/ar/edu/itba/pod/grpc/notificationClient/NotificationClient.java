package ar.edu.itba.pod.grpc.notificationClient;

import ar.edu.itba.pod.grpc.GenericClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    public static void main(String[] args) throws InterruptedException {
        logger.debug("Notification client started, mapping actions");
        GenericClient client = new GenericClient(new NotificationActionMapper());
        logger.debug("Notification actions mapped");
        client.run(args);
    }
}
