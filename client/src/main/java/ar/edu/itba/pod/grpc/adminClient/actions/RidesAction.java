package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AddAttractionRequest;
import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.CsvFileIterator;
import ar.edu.itba.pod.grpc.interfaces.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class RidesAction implements Action {

    private int attractionsAdded = 0;
    private int attractionsFailed = 0;

    private static final Logger logger = LoggerFactory.getLogger(RidesAction.class);

    @Override
    public Action execute(Arguments arguments) {
        if (arguments.getFilename() == null) {
            throw new IllegalClientArgumentException("The action rides needs a file to process, use -DinPath=filename");
        }

        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());

        CsvFileIterator fileIterator = new CsvFileIterator(arguments.getFilename());
        while (fileIterator.hasNext()) {
            String[] fields = fileIterator.next();
            if (fields.length == 4) {
                AddAttractionRequest request = AddAttractionRequest.newBuilder()
                        .setName(fields[0])
                        .setOpeningTime(fields[1])
                        .setClosingTime(fields[2])
                        .setSlotDurationMinutes(Integer.parseInt(fields[3]))
                        .build();
                logger.info("Sending ride request {}", request);
                try{
                    com.google.protobuf.BoolValue response = stub.addAttraction(request);
                    if (response.getValue()) {
                        logger.info("ride {} added", fields[0]);
                        attractionsAdded++;
                    } else {
                        logger.info("ride {} could not be added", fields[0]);
                        attractionsFailed++;
                    }
                }catch (Exception e) {
                    attractionsFailed++;
                }
            }
        }
        fileIterator.close();
        return this;
    }

    @Override
    public void showResults() {
        if(attractionsFailed!=0){
            System.out.printf("Cannot add %d attractions%n", attractionsFailed);
        }
        System.out.printf("%d attractions added%n", attractionsAdded);
    }
}
