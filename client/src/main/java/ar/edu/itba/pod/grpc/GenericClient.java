package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.exceptions.IOClientFileError;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.Parser;
import ar.edu.itba.pod.grpc.interfaces.ActionMapper;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GenericClient {

    private final ActionMapper actionMapper;
    private static final Logger logger = LoggerFactory.getLogger(GenericClient.class);
    private static final Map<String, String> errorMapper = Map.ofEntries(
            Map.entry("UNKNOWN", "Unknown error."),
            Map.entry("ATTRACTION_NOT_EXISTS", "Attraction does not exist."),
            Map.entry("INVALID_DAY", "Invalid day."),
            Map.entry("NEGATIVE_CAPACITY", "Capacity cannot be negative."),
            Map.entry("CAPACITY_ALREADY_DEFINED", "Capacity has already been loaded."),
            Map.entry("INVALID_SLOT", "Invalid slot."),
            Map.entry("RESERVATION_ALREADY_EXISTS", "Reservation already exists."),
            Map.entry("MISSING_PASS", "Client does not have a valid pass."),
            Map.entry("NO_CAPACITY", "No capacity available."),
            Map.entry("ALREADY_CONFIRMED", "Reservation already confirmed."),
            Map.entry("ATTRACTION_ALREADY_EXISTS", "Attraction already exists."),
            Map.entry("TICKET_ALREADY_EXISTS", "The user already has a ticket."),
            Map.entry("EMPTY_ATTRACTION", "Attraction name cannot be empty."),
            Map.entry("INVALID_TICKET_TYPE", "The given ticket type is invalid"),
            Map.entry("RESERVATION_NOT_FOUND", "Reservation not found."));
    ;

    public GenericClient(ActionMapper actionMapper){

        this.actionMapper = actionMapper;
    }

    public void run(String[] args) throws InterruptedException{
        Arguments arguments = null;
        try {

            arguments = Parser.parse(args);
            logger.debug("Parsed arguments: {}", arguments);

            actionMapper.getAction(arguments.getAction()).execute(arguments).showResults();
        }

        catch (IllegalClientArgumentException | IOClientFileError e) {
            System.out.println("Client error: " + e.getMessage());
        }
        catch (StatusRuntimeException e) {
            System.out.println("Server error received: " + errorMapper.getOrDefault(e.getStatus().getDescription(), "Unknown error."));
        }
        catch(Exception e) {
            System.out.println("Unknown error: " + e.getMessage());
        }

        finally {
            if (arguments != null && arguments.getChannel() != null) {
                arguments.getChannel().shutdown().awaitTermination(10, TimeUnit.SECONDS);
            }
        }
    }
}
