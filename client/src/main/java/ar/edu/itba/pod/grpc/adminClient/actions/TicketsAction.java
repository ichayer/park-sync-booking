package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.TicketRequest;
import ar.edu.itba.pod.grpc.adminClient.AdminArguments;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.CsvFileIterator;

import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class TicketsAction extends AdminAction {

    private int ticketsAdded = 0;
    private int ticketsFailed = 0;

    public TicketsAction(AdminArguments arguments){
        super(arguments);
    }

    @Override
    public void execute() {
        if (arguments.getFilename() == null) {
            throw new IllegalClientArgumentException("The action tickets needs a file to process, use -DinPath=filename");
        }

        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());

        CsvFileIterator fileIterator = new CsvFileIterator(arguments.getFilename());
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == 4) {
                TicketRequest.PassType passType = mapPassType(fields[1]);
                if (passType != TicketRequest.PassType.UNKNOWN) {
                    TicketRequest request = TicketRequest.newBuilder()
                            .setVisitorId(fields[0])
                            .setPassType(passType)
                            .setDayOfYear(Integer.parseInt(fields[2]))
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
    }

    @Override
    public void showResults() {
        if(ticketsFailed!=0){
            System.out.printf("Cannot add %d passes%n", ticketsFailed);
        }
        System.out.printf("%d passes added%n", ticketsAdded);
    }

    private static TicketRequest.PassType mapPassType(String type) {
        return switch (type) {
            case "HALFDAY" -> TicketRequest.PassType.HALFDAY;
            case "FULLDAY" -> TicketRequest.PassType.FULLDAY;
            case "UNLIMITED" -> TicketRequest.PassType.UNLIMITED;
            default -> TicketRequest.PassType.UNKNOWN;
        };
    }
}
