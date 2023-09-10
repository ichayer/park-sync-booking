package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.exceptions.*;
import ar.edu.itba.pod.grpc.server.handlers.AttractionHandler;
import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.models.TicketType;
import ar.edu.itba.pod.grpc.server.utils.ParseUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.util.UUID;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final AttractionHandler attractionHandler;

    public AdminServiceImpl(AttractionHandler attractionHandler) {
        this.attractionHandler = attractionHandler;
    }

    @Override
    public void addAttraction(AddAttractionRequest request, StreamObserver<Empty> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getName());
        LocalTime openTime = ParseUtils.parseTime(request.getOpeningTime());
        LocalTime closeTime = ParseUtils.parseTime(request.getClosingTime());
        int slotDuration = ParseUtils.checkValidDuration(request.getSlotDurationMinutes());

        if (!openTime.isBefore(closeTime))
            throw new InvalidOpeningAndClosingTimeException();

        attractionHandler.createAttraction(attractionName, openTime, closeTime, slotDuration);

        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(AddTicketRequest request, StreamObserver<Empty> responseObserver) {
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());

        TicketType ticketType = TicketType.fromPassType(request.getPassType()).orElseThrow(InvalidDayException::new);
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());

        attractionHandler.addTicket(visitorId, dayOfYear, ticketType);
        responseObserver.onCompleted();
    }

    @Override
    public void addCapacity(AddCapacityRequest request, StreamObserver<AddCapacityResponse> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getAttractionName());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());
        int capacity = ParseUtils.checkValidCapacity(request.getCapacity());

        DefineSlotCapacityResult result = attractionHandler.setSlotCapacityForAttraction(attractionName, dayOfYear, capacity);

        responseObserver.onNext(AddCapacityResponse.newBuilder()
                .setCancelledBookings(result.bookingsCancelled())
                .setConfirmedBookings(result.bookingsConfirmed())
                .setRelocatedBookings(result.bookingsRelocated())
                .build());
        responseObserver.onCompleted();
    }
}
