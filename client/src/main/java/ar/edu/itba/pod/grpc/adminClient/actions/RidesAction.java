package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.AttractionRequest;
import ar.edu.itba.pod.grpc.adminClient.AdminArguments;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.CsvFileIterator;


import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class RidesAction extends AdminAction {

    private int attractionsAdded = 0;
    private int attractionsFailed = 0;

    public RidesAction(AdminArguments arguments){
        super(arguments);
    }

    @Override
    public void execute() {
        if (arguments.getFilename() == null) {
            throw new IllegalClientArgumentException("The action rides needs a file to process, use -DinPath=filename");
        }

        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());

        CsvFileIterator fileIterator = new CsvFileIterator(arguments.getFilename());
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == 4) {
                AttractionRequest request = AttractionRequest.newBuilder()
                        .setName(fields[0])
                        .setHoursFrom(fields[1])
                        .setHoursTo(fields[2])
                        .setSlotGap(Integer.parseInt(fields[3]))
                        .build();
                // BooleanResponse response = stub.addAttraction(request);
                // if (response.getSuccess()) {
                //     attractionsAdded++;
                // } else {
                //     attractionsFailed++;
                // }
            }
        }
        fileIterator.close();
    }

    @Override
    public void showResults() {
        if(attractionsFailed!=0){
            System.out.printf("Cannot add %d attractions%n", attractionsFailed);
        }
        System.out.printf("%d attractions added%n", attractionsAdded);
    }
}
