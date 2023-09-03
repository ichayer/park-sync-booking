package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.PassType;
import ar.edu.itba.pod.grpc.TicketRequest;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.CsvFileIterator;
import ar.edu.itba.pod.grpc.interfaces.Action;

import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class TicketsAction implements Action {

    private int ticketsAdded = 0;
    private int ticketsFailed = 0;

    @Override
    public Action execute(Arguments arguments) {
        if (arguments.getFilename() == null) {
            throw new IllegalClientArgumentException("The action tickets needs a file to process, use -DinPath=filename");
        }

        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());

        CsvFileIterator fileIterator = new CsvFileIterator(arguments.getFilename());
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == 4) {
                PassType passType = mapPassType(fields[1]);
                if (passType != PassType.PASS_TYPE_UNKNOWN) {
                    TicketRequest request = TicketRequest.newBuilder()
                            .setVisitorId(fields[0])
                            .setPassType(passType)
                            .setDayOfYear(fields[2])
                            .build();
                    // BooleanResponse response = stub.addTicket(request);
                    // if (response.getSuccess()) {
                    //     ticketsAdded++;
                    // } else {
                    //     ticketsFailed++;
                    // }
                }
            }
        }
        fileIterator.close();
        return this;
    }

    @Override
    public void showResults() {
        if(ticketsFailed!=0){
            System.out.printf("Cannot add %d passes%n", ticketsFailed);
        }
        System.out.printf("%d passes added%n", ticketsAdded);
    }

    private static PassType mapPassType(String type) {
        return switch (type) {
            case "HALFDAY" -> PassType.PASS_TYPE_HALF_DAY;
            case "FULLDAY" -> PassType.PASS_TYPE_FULL_DAY;
            case "UNLIMITED" -> PassType.PASS_TYPE_UNLIMITED;
            default -> PassType.PASS_TYPE_UNKNOWN;
        };
    }
}
