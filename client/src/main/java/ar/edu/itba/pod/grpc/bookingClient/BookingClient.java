package ar.edu.itba.pod.grpc.bookingClient;

import ar.edu.itba.pod.grpc.GenericClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BookingClient {
    private static final Logger logger = LoggerFactory.getLogger(BookingClient.class);

    public static void main(String[] args) throws InterruptedException {
        logger.debug("Booking client started, mapping actions");
        GenericClient client = new GenericClient(new BookingActionMapper());
        logger.debug("Booking actions mapped");
        client.run(args);
    }
}
