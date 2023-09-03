//package ar.edu.itba.pod.grpc.server.services;
//
//import ar.edu.itba.pod.grpc.*;
//import ar.edu.itba.pod.grpc.server.models.Attraction;
//import io.grpc.stub.StreamObserver;
//
//import java.util.Collection;
//import java.util.Map;
//
//public class ReservationService extends ReservationServiceGrpc.ReservationServiceImplBase {
//
//    private final Map<String, Attraction> attractions;
//
//    public ReservationService(Map<String, Attraction> attractionsMap) {
//        this.attractions = attractionsMap;
//    }
//
//    @Override
//    public void attractionLookUp(AttractionLookUpRequest request, StreamObserver<AttractionLookUpResponse> responseObserver) {
//        Collection<AttractionData> attractionsData = attractions.values().stream().map(attraction -> AttractionData.newBuilder()
//                .setName(attraction.getName())
//                .setOpeningTime(attraction.getOpeningTime().toString())
//                .setClosingTime(attraction.getClosingTime().toString())
//                .setSlotGap(attraction.getSlotDuration())
//                .build()).toList();
//        responseObserver.onNext(AttractionLookUpResponse.newBuilder().addAllAttractions(attractionsData).build());
//        responseObserver.onCompleted();
//    }
//
//    @Override
//    public void attractionAvailabilityLookUp(AvailabilityRequest request, StreamObserver<AttractionResponse> responseObserver) {
//        super.attractionAvailabilityLookUp(request, responseObserver);
//    }
//
//    @Override
//    public void submitReservation(ReservationRequest request, StreamObserver<ReservationResponse> responseObserver) {
//        super.submitReservation(request, responseObserver);
//    }
//
//    @Override
//    public void confirmReservation(ConfirmReservationRequest request, StreamObserver<ConfirmReservationResponse> responseObserver) {
//        super.confirmReservation(request, responseObserver);
//    }
//
//    @Override
//    public void cancelReservation(CancelReservationRequest request, StreamObserver<CancelReservationResponse> responseObserver) {
//        super.cancelReservation(request, responseObserver);
//    }
//}