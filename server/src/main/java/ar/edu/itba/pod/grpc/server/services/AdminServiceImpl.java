package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.models.TicketType;
import ar.edu.itba.pod.grpc.server.utils.LocalTimeUtils;
import com.google.protobuf.BoolValue;
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
    public void addAttraction(AddAttractionRequest request, StreamObserver<BoolValue> responseObserver) {
        String attractionName = request.getName();
        Optional<LocalTime> openTime = LocalTimeUtils.parseTimeOrEmpty(request.getOpeningTime());
        Optional<LocalTime> closeTime = LocalTimeUtils.parseTimeOrEmpty(request.getClosingTime());
        int slotDuration = request.getSlotDurationMinutes();

        if (openTime.isEmpty() || closeTime.isEmpty() || slotDuration <= 0 || openTime.get().isAfter(closeTime.get()) || attractionName.isEmpty()) {
            responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
            responseObserver.onCompleted();
            return;
        }

        boolean success = attractionHandler.createAttraction(attractionName, openTime.get(), closeTime.get(), slotDuration);

        responseObserver.onNext(BoolValue.newBuilder().setValue(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(AddTicketRequest request, StreamObserver<BoolValue> responseObserver) {
        boolean success = false;
        int dayOfYear = request.getDayOfYear();
        Optional<TicketType> ticketType = TicketType.fromPassType(request.getPassType());

        if (dayOfYear >= 1 && dayOfYear <= 365 && ticketType.isPresent()) {
            UUID visitorId = UUID.fromString(request.getVisitorId());
            success = attractionHandler.addTicket(visitorId, dayOfYear, ticketType.get());
        }

        responseObserver.onNext(BoolValue.newBuilder().setValue(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addCapacity(AddCapacityRequest request, StreamObserver<AddCapacityResponse> responseObserver) {
        AddCapacityStatus status = null;

        if (request.getDayOfYear() < 1 || request.getDayOfYear() > 365) {
            status = AddCapacityStatus.ADD_CAPACITY_STATUS_INVALID_DAY;
        } else if (request.getCapacity() < 0) {
            status = AddCapacityStatus.ADD_CAPACITY_STATUS_NEGATIVE_CAPACITY;
        }

        if (status != null) {
            responseObserver.onNext(AddCapacityResponse.newBuilder().setStatus(status).build());
            responseObserver.onCompleted();
            return;
        }

        DefineSlotCapacityResult result = attractionHandler.setSlotCapacityForAttraction(request.getAttractionName(), request.getDayOfYear(), request.getCapacity());

        status = switch (result.status()) {
            case SUCCESS -> AddCapacityStatus.ADD_CAPACITY_STATUS_SUCCESS;
            case CAPACITY_ALREADY_SET -> AddCapacityStatus.ADD_CAPACITY_STATUS_CAPACITY_ALREADY_LOADED;
            case ATTRACTION_NOT_FOUND -> AddCapacityStatus.ADD_CAPACITY_STATUS_NOT_EXISTS;
        };

        responseObserver.onNext(AddCapacityResponse.newBuilder()
                .setStatus(status)
                .setCancelledBookings(result.bookingsCancelled())
                .setConfirmedBookings(result.bookingsConfirmed())
                .setRelocatedBookings(result.bookingsRelocated())
                .build());
        responseObserver.onCompleted();
    }
}
