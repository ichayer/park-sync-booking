package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.util.Map;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final Map<String, AttractionRequest> attractions;

    public AdminServiceImpl(Map<String, AttractionRequest> attractionsMap) {
        this.attractions = attractionsMap;
    }

    @Override
    public void addAttraction(AttractionRequest request, StreamObserver<BooleanResponse> responseObserver) {
        boolean isValid = isValidAttractionRequest(request);

        if (isValid) {
            String attractionName = request.getName();
            attractions.put(attractionName, request);
        }

        responseObserver.onNext(BooleanResponse.newBuilder().setSuccess(isValid).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(TicketRequest request, StreamObserver<BooleanResponse> responseObserver) {
        super.addTicket(request, responseObserver);
    }

    @Override
    public void addCapacity(CapacityRequest request, StreamObserver<CapacityResponse> responseObserver) {
        super.addCapacity(request, responseObserver);
    }

    private boolean isValidAttractionRequest(AttractionRequest request) {

        String attractionName = request.getName();

        if (attractionName.isEmpty() || attractions.containsKey(attractionName)) {
            return false;
        }

        int slotGap = request.getSlotGap();
        if (slotGap <= 0 || slotGap > 60) {
            return false;
        }

        String hoursFrom = request.getHoursFrom();
        String hoursTo = request.getHoursTo();
        if (!hoursFrom.matches("\\d{2}:\\d{2}") || !hoursTo.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        LocalTime openTime, closeTime;

        try {
            openTime = LocalTime.parse(hoursFrom);
            closeTime = LocalTime.parse(hoursTo);
        } catch (Exception e) {
            return false;
        }

        return !openTime.isAfter(closeTime);
    }
}
