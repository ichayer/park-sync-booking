package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.utils.LocalTimeUtils;
import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Optional;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final DataHandler dataHandler;

    public AdminServiceImpl(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void addAttraction(AddAttractionRequest request, StreamObserver<BoolValue> responseObserver) {
        boolean success = false;
        String attractionName = request.getName();
        Optional<LocalTime> openTime = LocalTimeUtils.parseTimeOrEmpty(request.getOpeningTime());
        Optional<LocalTime> closeTime = LocalTimeUtils.parseTimeOrEmpty(request.getClosingTime());
        int slotGap = request.getSlotDurationMinutes();

        if (openTime.isPresent() && closeTime.isPresent() && isValidAddAttractionRequest(attractionName, openTime.get(), closeTime.get(), slotGap)) {
            Attraction attraction = new Attraction(attractionName, openTime.get(), closeTime.get(), slotGap);
            success = dataHandler.addAttraction(attractionName, attraction);
        }
        responseObserver.onNext(BoolValue.newBuilder().setValue(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(AddTicketRequest request, StreamObserver<BoolValue> responseObserver) {
        boolean success = false;
        int dayOfYear = request.getDayOfYear();

        // TODO: Check if PassType.forNumber(request.getPassType().getNumber() != null) validation is necessary
        if (dayOfYear >= 1 && dayOfYear <= 365 ) {
            success = dataHandler.addTicket(request.getVisitorId(), dayOfYear, request.getPassType());
        }

        responseObserver.onNext(BoolValue.newBuilder().setValue(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addCapacity(AddCapacityRequest request, StreamObserver<AddCapacityResponse> responseObserver) {
        AddCapacityStatus status = null;
        int confirmedBookings = 0, relocatedBookings = 0, cancelledBookings = 0;

        if (!dataHandler.containsAttraction(request.getAttractionName())) {
            status = AddCapacityStatus.ADD_CAPACITY_STATUS_NOT_EXISTS;
        } else if (request.getDayOfYear() < 1 || request.getDayOfYear() > 365) {
            status = AddCapacityStatus.ADD_CAPACITY_STATUS_INVALID_DAY;
        } else if (request.getCapacity() < 0) {
            status = AddCapacityStatus.ADD_CAPACITY_STATUS_NEGATIVE_CAPACITY;
        } else {
            if (dataHandler.setAttractionCapacityByDate(request.getAttractionName(), request.getDayOfYear(), request.getCapacity())) {
                status = AddCapacityStatus.ADD_CAPACITY_STATUS_SUCCESS;;
                // TODO: Set confirmedBookings, relocatedBookings, cancelledBookings variables.
            } else {
                status = AddCapacityStatus.ADD_CAPACITY_STATUS_CAPACITY_ALREADY_LOADED;
            }
        }

        // TODO: Confirm, cancel or assign another attraction to the visitor
        responseObserver.onNext(AddCapacityResponse.newBuilder()
                .setCancelledBookings(cancelledBookings)
                .setConfirmedBookings(confirmedBookings)
                .setRelocatedBookings(relocatedBookings)
                .setStatus(status)
                .build());
        responseObserver.onCompleted();
    }

    private boolean isValidAddAttractionRequest(String attractionName, LocalTime openTime, LocalTime closeTime, int slotGap) {
        return !attractionName.isEmpty() &&
                slotGap > 0 && slotGap <= 60 &&
                openTime.isBefore(closeTime);
    }
}
