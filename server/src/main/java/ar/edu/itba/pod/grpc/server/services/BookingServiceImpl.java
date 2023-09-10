package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.exceptions.EmptyAttractionException;
import ar.edu.itba.pod.grpc.server.exceptions.InvalidDayException;
import ar.edu.itba.pod.grpc.server.exceptions.InvalidSlotException;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.models.Reservation;
import ar.edu.itba.pod.grpc.server.results.MakeReservationResult;
import ar.edu.itba.pod.grpc.server.utils.LocalTimeUtils;
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
                        .setClosingTime(LocalTimeUtils.formatTime(attraction.getClosingTime()))
                        .setOpeningTime(LocalTimeUtils.formatTime(attraction.getOpeningTime()))
                        .build()).toList();

        responseObserver.onNext(GetAttractionsResponse.newBuilder().addAllAttraction(attractionsDto).build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkAttractionAvailability(AvailabilityRequest request, StreamObserver<AvailabilityResponse> responseObserver) {
        String attractionName = request.getAttractionName();
        int dayOfYear = request.getDayOfYear();
        Optional<LocalTime> slotFrom = LocalTimeUtils.parseTimeOrEmpty(request.getSlotFrom());
        Optional<LocalTime> slotTo = LocalTimeUtils.parseTimeOrEmpty(request.getSlotTo());

        List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

        if (dayOfYear < 0 || dayOfYear > 365) {
            throw new InvalidDayException();
        } else if (slotFrom.isEmpty() || (slotTo.isPresent() && slotFrom.get().isAfter(slotTo.get()))) {
            throw new InvalidSlotException();
        } else if (!attractionName.isEmpty() /*&& !attractionHandler.containsAttraction(attractionName)*/) {
            throw new EmptyAttractionException();
        }

        // TODO: Implement methods
        if (!attractionName.isEmpty() && slotTo.isPresent()) {
            // availabilitySlots.addAll(attractionHandler.getAvailabilityForAttraction(attractionName, dayOfYear, slotFrom.get(), slotTo.get()));
        } else if (attractionName.isEmpty() && slotTo.isPresent()) {
            // availabilitySlots.addAll(attractionHandler.getAvailabilityForAllAttractions(dayOfYear, slotFrom.get(), slotTo.get()));
        } else if (!attractionName.isEmpty()) {
            // availabilitySlots.addAll(attractionHandler.getAvailabilityForSingleSlot(attractionName, dayOfYear, slotFrom.get()));
        }

        responseObserver.onNext(AvailabilityResponse.newBuilder().addAllSlot(availabilitySlots).build());
        responseObserver.onCompleted();
    }

    @Override
    public void reserveAttraction(BookingRequest request, StreamObserver<ReservationResponse> responseObserver) {
        String attractionName = request.getAttractionName();
        int dayOfYear = request.getDayOfYear();
        Optional<LocalTime> slotTime = LocalTimeUtils.parseTimeOrEmpty(request.getSlot());
        UUID visitorId = UUID.fromString(request.getVisitorId());

        if (dayOfYear <= 0 || dayOfYear > 365) {
            throw new InvalidDayException();
        } else if (slotTime.isEmpty()) {
            throw new InvalidSlotException();
        } else if (attractionName.isEmpty()) {
            throw new EmptyAttractionException();
        }


        BookingState bookingState;
        MakeReservationResult result = attractionHandler.makeReservation(attractionName, visitorId, dayOfYear, slotTime.get());
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
