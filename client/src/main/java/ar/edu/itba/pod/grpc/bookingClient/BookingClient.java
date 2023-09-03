package ar.edu.itba.pod.grpc.bookingClient;

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
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws InterruptedException {
        Arguments arguments = null;
        try {
            arguments = Parser.parse(args);
            BookingActionMapper actionMapper = new BookingActionMapper();
            actionMapper.getAction(arguments.getAction()).execute(arguments).showResults();
        } catch (IllegalClientArgumentException | IOClientFileError e) {
            //TODO: improve
            System.out.println(e.getMessage());
        } finally {
            if (arguments != null && arguments.getChannel() != null) {
                arguments.getChannel().shutdown().awaitTermination(10, TimeUnit.SECONDS);
            }
        }
    }
}
