package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import io.grpc.stub.StreamObserver;

public class ReservationService extends ReservationServiceGrpc.ReservationServiceImplBase {

    @Override
    public void attractionLookUp(AttractionLookUpRequest request, StreamObserver<AttractionLookUpResponse> responseObserver) {
        super.attractionLookUp(request, responseObserver);
    }

    @Override
    public void attractionAvailabilityLookUp(AvailabilityRequest request, StreamObserver<AttractionResponse> responseObserver) {
        super.attractionAvailabilityLookUp(request, responseObserver);
    }

    @Override
    public void submitReservation(ReservationRequest request, StreamObserver<ReservationResponse> responseObserver) {
        super.submitReservation(request, responseObserver);
    }

    @Override
    public void confirmReservation(ConfirmReservationRequest request, StreamObserver<ConfirmReservationResponse> responseObserver) {
        super.confirmReservation(request, responseObserver);
    }

    @Override
    public void cancelReservation(CancelReservationRequest request, StreamObserver<CancelReservationResponse> responseObserver) {
        super.cancelReservation(request, responseObserver);
    }
}