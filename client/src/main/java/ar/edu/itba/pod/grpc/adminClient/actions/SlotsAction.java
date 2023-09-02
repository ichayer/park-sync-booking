package ar.edu.itba.pod.grpc.adminClient.actions;

import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.CapacityRequest;
import ar.edu.itba.pod.grpc.adminClient.AdminArguments;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.interfaces.Action;

import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class SlotsAction extends AdminAction{
    private int noChangedBookings;
    private int relocatedBookings;
    private int cancelledBookings;

    public SlotsAction(AdminArguments arguments) {
        super(arguments);
    }

    @Override
    public Action execute() {
        AdminServiceGrpc.AdminServiceBlockingStub stub = newBlockingStub(arguments.getChannel());
        if (arguments.getDayOfYear() == null || arguments.getRideName() == null || arguments.getCapacity() == null) {
            throw new IllegalClientArgumentException("The slots action must be provided a day, a name and a capacity " +
                    "with the arguments -Dday=day -Dride=rideName -Dcapacity=capacity");
        }
        // TODO: implement response on server side, initialize the fields of the class
        CapacityRequest request = CapacityRequest.newBuilder()
                .setRide(arguments.getRideName())
                .setDay(arguments.getDayOfYear())
                .setCapacity(arguments.getCapacity())
                .build();
        //CapacityResponse response = stub.addCapacity(request);

        return this;
    }

    @Override
    public void showResults() {
        System.out.printf("Loaded capacity of %d for %s on day %d%n", arguments.getCapacity(), arguments.getRideName(), arguments.getDayOfYear());
        System.out.printf("%d bookings confirmed without changes%n", noChangedBookings);
        System.out.printf("%d bookings relocated%n", relocatedBookings);
        System.out.printf("%d bookings cancelled%n", cancelledBookings);
    }
}
