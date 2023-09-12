package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.exceptions.EmptyAttractionException;
import ar.edu.itba.pod.grpc.server.exceptions.CheckAvailabilityInvalidArgumentException;
import ar.edu.itba.pod.grpc.server.exceptions.InvalidSlotException;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.handlers.AttractionHandler;
import ar.edu.itba.pod.grpc.server.results.AttractionAvailabilityResult;
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
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());
        LocalTime slotFrom = ParseUtils.parseTime(request.getSlotFrom());
        LocalTime slotTo;

        String attractionName = ParseUtils.checkAttractionNameOrNull(request.getAttractionName());
        slotTo = ParseUtils.parseTimeOrNull(request.getSlotTo());
        if (attractionName == null && slotTo == null)
            throw new CheckAvailabilityInvalidArgumentException();

        if (slotTo != null && slotFrom.isAfter(slotTo))
            throw new InvalidSlotException();

        Collection<AttractionAvailabilityResult> availabilityResults;
        if (attractionName != null) {
            availabilityResults = attractionHandler.getAvailabilityForAttraction(attractionName, dayOfYear, slotFrom, slotTo);
        } else {
            availabilityResults = attractionHandler.getAvailabilityForAllAttractions(dayOfYear, slotFrom, slotTo);
        }

        Collection<AvailabilitySlot> availabilitySlotsDto = availabilityResults.stream()
                .map(availabilityResult -> AvailabilitySlot.newBuilder()
                        .setAttractionName(availabilityResult.attractionName())
                        .setSlot(ParseUtils.formatTime(availabilityResult.slotTime()))
                        .setSlotCapacity(availabilityResult.slotCapacity())
                        .setBookingsConfirmed(availabilityResult.confirmedReservations())
                        .setBookingsPending(availabilityResult.pendingReservations())
                        .build()).toList();

        responseObserver.onNext(AvailabilityResponse.newBuilder().addAllSlot(availabilitySlotsDto).build());
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
        String attractionName = ParseUtils.checkAttractionName(request.getAttractionName());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());
        LocalTime slotTime = ParseUtils.parseTime(request.getSlot());
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());

        attractionHandler.confirmReservation(attractionName, visitorId, dayOfYear, slotTime);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancelReservation(BookingRequest request, StreamObserver<Empty> responseObserver) {
        String attractionName = ParseUtils.checkAttractionName(request.getAttractionName());
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());
        LocalTime slotTime = ParseUtils.parseTime(request.getSlot());
        UUID visitorId = ParseUtils.parseId(request.getVisitorId());

        attractionHandler.cancelReservation(attractionName, visitorId, dayOfYear, slotTime);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
