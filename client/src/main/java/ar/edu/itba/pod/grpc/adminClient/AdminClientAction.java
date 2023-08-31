package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public enum AdminClientAction {
    RIDES(){
        @Override
        public void execute(AdminGrpc.AdminBlockingStub stub, AdminArguments arguments) {

            if(arguments.getFilename() == null){
                throw new IllegalClientArgumentException("The action rides must provide a file to read the rides from using -DinPath=fileName");
            }

            int attractionsAdded = 0;
            int attractionsFailed = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(arguments.getFilename()))) {
                String line;
                reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(";");
                    if (fields.length == 4) {
                        // TODO: implement response on server side
                        AttractionRequest request = AttractionRequest.newBuilder()
                                .setName(fields[0])
                                .setHoursFrom(fields[1])
                                .setHoursTo(fields[2])
                                .setSlotGap(Integer.parseInt(fields[3]))
                                .build();

//                        BooleanResponse response = stub.addAttraction(request);
//                        if (response.getSuccess()) {
//                            attractionsAdded++;
//                        } else {
//                            attractionsFailed++;
//                            System.out.println("Cannot add attraction: " + response.getMessage());
//                        }

                    }
                }
            } catch (IOException e) {
                //TODO: define how we will handle this exceptions
                e.printStackTrace();
            }
            // TODO: define if the message should be printed here
        }
    },
    TICKETS(){
        @Override
        public void execute(AdminGrpc.AdminBlockingStub stub, AdminArguments arguments) {

            if(arguments.getFilename() == null){
                throw new IllegalClientArgumentException("The action tickets must provide a file to read the rides from using -DinPath=fileName");
            }

            int ticketsAdded = 0;
            int ticketsFailed = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(arguments.getFilename()))) {
                String line;
                reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(";");
                    TicketRequest.PassType passType;
                    if (fields.length == 3) {
                        switch (fields[1]) {
                            case "HALFDAY":
                                passType = TicketRequest.PassType.PASSTYPE_HALFDAY;
                                break;
                            case "FULLDAY":
                                passType = TicketRequest.PassType.PASSTYPE_FULLDAY;
                                break;
                            case "UNLIMITED":
                                passType = TicketRequest.PassType.PASSTYPE_UNLIMITED;
                                break;
                            default:
                                passType = TicketRequest.PassType.PASSTYPE_UNKNOWN;
                                break;
                        }

                        if (passType != TicketRequest.PassType.PASSTYPE_UNKNOWN) {
                            TicketRequest request = TicketRequest.newBuilder()
                                    .setVisitorId(fields[0])
                                    .setPassType(passType)
                                    .setDayOfYear(Integer.parseInt(fields[2]))
                                    .build();

    //                        BooleanResponse response = stub.addTicket(request);
    //                        if (response.getSuccess()) {
    //                            ticketsAdded++;
    //                        } else {
    //                            ticketsFailed++;
    //                        }`
                        }
                    }
                }
            } catch (Exception e) {
                //TODO: define how we will handle this exceptions
                e.printStackTrace();
            }
            // TODO: define if the message should be printed here
        }
    },
    SLOTS(){
        @Override
        public void execute(AdminGrpc.AdminBlockingStub stub, AdminArguments arguments) {
            if(arguments.getDayOfYear() == null || arguments.getRideName() == null || arguments.getCapacity() == null){
                throw new IllegalClientArgumentException("The slots action must be provided a day, a name and a capacity " +
                        "with the arguments -Dday=day -Dride=rideName -Dcapacity=capacity");
            }
            // TODO: implement response on server side
            CapacityRequest request = CapacityRequest.newBuilder()
                    .setRide(arguments.getRideName())
                    .setDay(arguments.getDayOfYear())
                    .setCapacity(arguments.getCapacity())
                    .build();
            //CapacityResponse response = stub.addCapacity(request);

            // TODO: define if the message should be printed here
        }
    };

    public abstract void execute(AdminGrpc.AdminBlockingStub stub, AdminArguments arguments);

    public static AdminClientAction getAction(String arg) {
        for (AdminClientAction actions : AdminClientAction.values()) {
            if (actions.name().equals(arg)) {
                return actions;
            }
        }
        throw new RuntimeException(arg + "is not a valid Action");
    }
}
