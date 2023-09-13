package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AddTicketRequest;
import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.PassType;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.CsvFileIterator;
import ar.edu.itba.pod.grpc.interfaces.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class TicketsAction implements Action {

    private int ticketsAdded = 0;
    private int ticketsFailed = 0;

    private static final Logger logger = LoggerFactory.getLogger(TicketsAction.class);

    @Override
    public Action execute(Arguments arguments) {
        if (arguments.getFilename() == null) {
            throw new IllegalClientArgumentException("The action tickets needs a file to process, use -DinPath=filename");
        }

        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());

        CsvFileIterator fileIterator = new CsvFileIterator(arguments.getFilename());
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length != 3) {
                logger.error("Invalid file format, got {} fields, expected 3 ", fields.length);
                continue;
            }
            PassType passType = mapPassType(fields[1]);
            if (passType == PassType.PASS_TYPE_UNKNOWN) {
                logger.error("Unknown pass type {}", fields[1]);
                continue;
            }
            AddTicketRequest request = AddTicketRequest.newBuilder()
                    .setVisitorId(fields[0])
                    .setPassType(passType)
                    .setDayOfYear(Integer.parseInt(fields[2]))
                    .build();
            logger.info("Sending ticket request {}", request);

            try {
                stub.addTicket(request);
                logger.info("ticket for user {} added", fields[0]);
                ticketsAdded++;
            } catch (Exception e) {
                logger.info("ticket for user {} could not be added", fields[0]);
                logger.info("reason: {}", e.getMessage());
                ticketsFailed++;
            }
        }
        fileIterator.close();
        return this;
    }

    @Override
    public void showResults() {
        if (ticketsFailed != 0) {
            System.out.printf("Cannot add %d passes%n", ticketsFailed);
        }
        System.out.printf("%d passes added%n", ticketsAdded);
    }

    private static PassType mapPassType(String type) {
        return switch (type) {
            case "HALF_DAY" -> PassType.PASS_TYPE_HALF_DAY;
            case "THREE" -> PassType.PASS_TYPE_FULL_DAY;
            case "UNLIMITED" -> PassType.PASS_TYPE_UNLIMITED;
            default -> PassType.PASS_TYPE_UNKNOWN;
        };
    }
}
