package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;

import java.io.IOException;

import static ar.edu.itba.pod.grpc.helpers.CsvFileProcessor.processFile;

public enum AdminClientAction {
    RIDES() {
        @Override
        public void execute(AdminServiceGrpc.AdminServiceBlockingStub stub, AdminArguments arguments) {
            try {
                processFile(arguments.getFilename(), stub, (fields) -> {
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
                        //     System.out.println("Cannot add attraction: " + response.getMessage());
                        // }
                    }
                });
            } catch (IOException e) {
                //TODO: define how we will handle this exceptions
                throw new RuntimeException(e);
            }
            // TODO: define if the message should be printed here
        }
    },
    TICKETS() {
        @Override
        public void execute(AdminServiceGrpc.AdminServiceBlockingStub stub, AdminArguments arguments) {
            try {
                processFile(arguments.getFilename(), stub, (fields) -> {
                    if (fields.length == 3) {
                        PassType passType = mapPassType(fields[1]);
                        if (passType != PassType.UNKNOWN) {
                            TicketRequest request = TicketRequest.newBuilder()
                                    .setVisitorId(fields[0])
                                    .setPassType(passType)
                                    .setDayOfYear(String.valueOf(Integer.parseInt(fields[2])))
                                    .build();
                            // BooleanResponse response = stub.addTicket(request);
                            // if (response.getSuccess()) {
                            //     ticketsAdded++;
                            // } else {
                            //     ticketsFailed++;
                            // }
                        }
                    }
                });
            } catch (Exception e) {
                //TODO: define how we will handle this exceptions
                e.printStackTrace();
            }
            // TODO: define if the message should be printed here
        }
    },
    SLOTS() {
        @Override
        public void execute(AdminServiceGrpc.AdminServiceBlockingStub stub, AdminArguments arguments) {
            if (arguments.getDayOfYear() == null || arguments.getRideName() == null || arguments.getCapacity() == null) {
                throw new IllegalClientArgumentException("The slots action must be provided a day, a name and a capacity " +
                        "with the arguments -Dday=day -Dride=rideName -Dcapacity=capacity");
            }
            // TODO: implement response on server side
            CapacityRequest request = CapacityRequest.newBuilder()
                    .setAttractionName(arguments.getRideName())
                    .setDayOfYear(arguments.getDayOfYear())
                    .setCapacity(arguments.getCapacity())
                    .build();
            //CapacityResponse response = stub.addCapacity(request);

            // TODO: define if the message should be printed here
        }
    };

    //TODO: IDK where this function should go
    private static PassType mapPassType(String type) {
        return switch (type) {
            case "HALFDAY" -> PassType.HALFDAY;
            case "FULLDAY" -> PassType.FULLDAY;
            case "UNLIMITED" -> PassType.UNLIMITED;
            default -> PassType.UNKNOWN;
        };
    }

    public static AdminClientAction getAction(String arg) {
        for (AdminClientAction actions : AdminClientAction.values()) {
            if (actions.name().equals(arg)) {
                return actions;
            }
        }
        throw new RuntimeException(arg + "is not a valid Action");
    }

    public abstract void execute(AdminServiceGrpc.AdminServiceBlockingStub stub, AdminArguments arguments);
}
