package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.models.MakeReservationResult;
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

        CheckAvailabilityStatus status = null;
        List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

        if (dayOfYear < 0 || dayOfYear > 365) {
            status = CheckAvailabilityStatus.CHECK_AVAILABILITY_INVALID_DAY;
        } else if (slotFrom.isEmpty() || (slotTo.isPresent() && slotFrom.get().isAfter(slotTo.get()))) {
            status = CheckAvailabilityStatus.CHECK_AVAILABILITY_INVALID_SLOT;
        } else if (!attractionName.isEmpty() /*&& !attractionHandler.containsAttraction(attractionName)*/) {
            status = CheckAvailabilityStatus.CHECK_AVAILABILITY_ATTRACTION_NOT_FOUND;
        }

        // TODO: Implement methods
        if (status != null) {
            if (!attractionName.isEmpty() && slotTo.isPresent()) {
                // availabilitySlots.addAll(attractionHandler.getAvailabilityForAttraction(attractionName, dayOfYear, slotFrom.get(), slotTo.get()));
            } else if (attractionName.isEmpty() && slotTo.isPresent()) {
                // availabilitySlots.addAll(attractionHandler.getAvailabilityForAllAttractions(dayOfYear, slotFrom.get(), slotTo.get()));
            } else if (!attractionName.isEmpty()) {
                // availabilitySlots.addAll(attractionHandler.getAvailabilityForSingleSlot(attractionName, dayOfYear, slotFrom.get()));
            } else {
                status = CheckAvailabilityStatus.CHECK_AVAILABILITY_UNKNOWN;
            }
        }
        responseObserver.onNext(AvailabilityResponse.newBuilder().setStatus(status).addAllSlot(availabilitySlots).build());
        responseObserver.onCompleted();
    }

    @Override
    public void reserveAttraction(BookingRequest request, StreamObserver<ReservationResponse> responseObserver) {
        String attractionName = request.getAttractionName();
        int dayOfYear = request.getDayOfYear();
        Optional<LocalTime> slotTime = LocalTimeUtils.parseTimeOrEmpty(request.getSlot());
        UUID visitorId = UUID.fromString(request.getVisitorId());

        ReservationStatus status = null;
        if (dayOfYear <= 0 || dayOfYear > 365) {
            status = ReservationStatus.BOOKING_STATUS_INVALID_DAY;
        } else if (slotTime.isEmpty()) {
            status = ReservationStatus.BOOKING_STATUS_INVALID_SLOT;
        } else if (attractionName.isEmpty()) {
            status = ReservationStatus.BOOKING_STATUS_ATTRACTION_NOT_FOUND;
        }

        if (status != null) {
            responseObserver.onNext(ReservationResponse.newBuilder().setStatus(status).build());
            responseObserver.onCompleted();
            return;
        }

        BookingState bookingState;
        MakeReservationResult result = attractionHandler.makeReservation(attractionName, visitorId, dayOfYear, slotTime.get());
        if (result.isSuccess()) {
            status = ReservationStatus.BOOKING_STATUS_SUCCESS;
            bookingState = switch (result.status()) {
                case QUEUED -> BookingState.RESERVATION_STATUS_PENDING;
                case CONFIRMED -> BookingState.RESERVATION_STATUS_CONFIRMED;
                default -> BookingState.RESERVATION_STATUS_UNKNOWN;
            };
        } else {
            bookingState = BookingState.RESERVATION_STATUS_UNKNOWN;
            status = switch (result.status()) {
                case MISSING_PASS -> ReservationStatus.BOOKING_STATUS_MISSING_PASS;
                case ALREADY_EXISTS -> ReservationStatus.BOOKING_STATUS_ALREADY_EXISTS;
                case ATTRACTION_NOT_FOUND -> ReservationStatus.BOOKING_STATUS_ATTRACTION_NOT_FOUND;
                case INVALID_SLOT_TIME -> ReservationStatus.BOOKING_STATUS_INVALID_SLOT;
                case OUT_OF_CAPACITY -> ReservationStatus.BOOKING_STATUS_NO_CAPACITY;
                default -> ReservationStatus.BOOKING_STATUS_UNKNOWN;
            };
        }

        responseObserver.onNext(ReservationResponse.newBuilder().setStatus(status).setState(bookingState).build());
        responseObserver.onCompleted();
    }

    @Override
    public void confirmReservation(BookingRequest request, StreamObserver<ConfirmationResponse> responseObserver) {
        super.confirmReservation(request, responseObserver);
    }

    @Override
    public void cancelReservation(BookingRequest request, StreamObserver<CancellationResponse> responseObserver) {
        super.cancelReservation(request, responseObserver);
    }
}
