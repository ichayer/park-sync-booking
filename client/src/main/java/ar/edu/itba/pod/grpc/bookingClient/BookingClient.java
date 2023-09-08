package ar.edu.itba.pod.grpc.bookingClient;

import ar.edu.itba.pod.grpc.GenericClient;
import ar.edu.itba.pod.grpc.adminClient.AdminActionMapper;
import ar.edu.itba.pod.grpc.adminClient.AdminClient;
import ar.edu.itba.pod.grpc.exceptions.IOClientFileError;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BookingClient {
    private static final Logger logger = LoggerFactory.getLogger(BookingClient.class);

    public static void main(String[] args) throws InterruptedException {
        logger.debug("Booking client started, mapping actions");
        GenericClient client = new GenericClient(new BookingActionMapper());
        logger.debug("Booking actions mapper");
        client.run(args);
    }
}
