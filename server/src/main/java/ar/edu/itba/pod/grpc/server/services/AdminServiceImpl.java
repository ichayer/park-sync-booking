package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.exceptions.*;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.models.TicketType;
import ar.edu.itba.pod.grpc.server.utils.LocalTimeUtils;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final AttractionHandler attractionHandler;

    public AdminServiceImpl(AttractionHandler attractionHandler) {
        this.attractionHandler = attractionHandler;
    }

    @Override
    public void addAttraction(AddAttractionRequest request, StreamObserver<Empty> responseObserver) {
        String attractionName = request.getName();
        Optional<LocalTime> openTime = LocalTimeUtils.parseTimeOrEmpty(request.getOpeningTime());
        Optional<LocalTime> closeTime = LocalTimeUtils.parseTimeOrEmpty(request.getClosingTime());
        int slotDuration = request.getSlotDurationMinutes();

        if (openTime.isEmpty() || closeTime.isEmpty() || slotDuration <= 0 || openTime.get().isAfter(closeTime.get())) {
            throw new InvalidSlotException();
        }

        if(attractionName.isEmpty()){
            throw new EmptyAttractionException();
        }

        attractionHandler.createAttraction(attractionName, openTime.get(), closeTime.get(), slotDuration);

        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(AddTicketRequest request, StreamObserver<Empty> responseObserver) {
        int dayOfYear = request.getDayOfYear();

        if(dayOfYear < 1 || dayOfYear > 365){
            throw new InvalidDayException();
        }

        Optional<TicketType> ticketType = TicketType.fromPassType(request.getPassType());
        UUID visitorId = UUID.fromString(request.getVisitorId());
        attractionHandler.addTicket(visitorId, dayOfYear, ticketType.orElseThrow(InvalidDayException::new));

        responseObserver.onCompleted();
    }

    @Override
    public void addCapacity(AddCapacityRequest request, StreamObserver<AddCapacityResponse> responseObserver) {

        if (request.getDayOfYear() < 1 || request.getDayOfYear() > 365) {
            throw new InvalidDayException();
        } else if (request.getCapacity() < 0) {
            throw new NegativeCapacityException();
        }


        DefineSlotCapacityResult result = attractionHandler.setSlotCapacityForAttraction(request.getAttractionName(), request.getDayOfYear(), request.getCapacity());


        responseObserver.onNext(AddCapacityResponse.newBuilder()
                .setCancelledBookings(result.bookingsCancelled())
                .setConfirmedBookings(result.bookingsConfirmed())
                .setRelocatedBookings(result.bookingsRelocated())
                .build());
        responseObserver.onCompleted();
    }
}
