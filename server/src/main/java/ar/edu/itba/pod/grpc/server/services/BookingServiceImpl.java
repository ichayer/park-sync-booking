package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.exceptions.EmptyAttractionException;
import ar.edu.itba.pod.grpc.server.exceptions.InvalidDayException;
import ar.edu.itba.pod.grpc.server.exceptions.InvalidSlotException;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.results.MakeReservationResult;
import ar.edu.itba.pod.grpc.server.utils.ParseUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.util.*;

public class BookingServiceImpl extends BookingServiceGrpc.BookingServiceImplBase {

    private final AttractionHandler attractionHandler;

    public BookingServiceImpl(AttractionHandler attractionHandler) {
        this.attractionHandler = attractionHandler;
    }

    @Override
    public void getAttractions(Empty request, StreamObserver<GetAttractionsResponse> responseObserver) {
        Collection<Attraction> attractions = attractionHandler.getAttractions();

        Collection<ar.edu.itba.pod.grpc.Attraction> attractionsDto = attractions.stream()
                .map(attraction -> ar.edu.itba.pod.grpc.Attraction.newBuilder()
                        .setName(attraction.getName())
                        .setClosingTime(ParseUtils.formatTime(attraction.getClosingTime()))
                        .setOpeningTime(ParseUtils.formatTime(attraction.getOpeningTime()))
                        .build()).toList();

        responseObserver.onNext(GetAttractionsResponse.newBuilder().addAllAttraction(attractionsDto).build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkAttractionAvailability(AvailabilityRequest request, StreamObserver<AvailabilityResponse> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getAttractionName());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());
        LocalTime slotFrom = ParseUtils.parseTime(request.getSlotFrom());
        LocalTime slotTo = ParseUtils.parseTime(request.getSlotTo());

        if (slotFrom.isAfter(slotTo))
            throw new InvalidSlotException();

        List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

        // TODO: Implement methods
        /*if (!attractionName.isEmpty() && slotTo.isPresent()) {
            // availabilitySlots.addAll(attractionHandler.getAvailabilityForAttraction(attractionName, dayOfYear, slotFrom.get(), slotTo.get()));
        } else if (attractionName.isEmpty() && slotTo.isPresent()) {
            // availabilitySlots.addAll(attractionHandler.getAvailabilityForAllAttractions(dayOfYear, slotFrom.get(), slotTo.get()));
        } else if (!attractionName.isEmpty()) {
            // availabilitySlots.addAll(attractionHandler.getAvailabilityForSingleSlot(attractionName, dayOfYear, slotFrom.get()));
        }*/

        responseObserver.onNext(AvailabilityResponse.newBuilder().addAllSlot(availabilitySlots).build());
        responseObserver.onCompleted();
    }

    @Override
    public void reserveAttraction(BookingRequest request, StreamObserver<ReservationResponse> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getAttractionName());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());
        LocalTime slotTime = ParseUtils.parseTime(request.getSlot());
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());

        BookingState bookingState;
        MakeReservationResult result = attractionHandler.makeReservation(attractionName, visitorId, dayOfYear, slotTime);
        bookingState = result.isConfirmed() ? BookingState.RESERVATION_STATUS_CONFIRMED : BookingState.RESERVATION_STATUS_PENDING;

        responseObserver.onNext(ReservationResponse.newBuilder().setState(bookingState).build());
        responseObserver.onCompleted();
    }

    @Override
    public void confirmReservation(BookingRequest request, StreamObserver<Empty> responseObserver) {
        super.confirmReservation(request, responseObserver);
    }

    @Override
    public void cancelReservation(BookingRequest request, StreamObserver<Empty> responseObserver) {
        super.cancelReservation(request, responseObserver);
    }
}
