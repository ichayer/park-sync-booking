package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.utils.LocalTimeUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BookingService extends BookingServiceGrpc.BookingServiceImplBase {

    private final DataHandler dataHandler;

    public BookingService(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void getAttractions(Empty request, StreamObserver<GetAttractionsResponse> responseObserver) {
        Collection<Attraction> attractions = dataHandler.getAttractions();

        Collection<ar.edu.itba.pod.grpc.Attraction> attractionsDto = attractions.stream()
                .map(attraction -> ar.edu.itba.pod.grpc.Attraction.newBuilder()
                .setName(attraction.getName())
                .setClosingTime(attraction.getClosingTime().toString())
                .setOpeningTime(attraction.getOpeningTime().toString())
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
        } else if (!attractionName.isEmpty() && !dataHandler.containsAttraction(attractionName)) {
            status = CheckAvailabilityStatus.CHECK_AVAILABILITY_ATTRACTION_NOT_FOUND;
        }

        // TODO: Implement methods
        if (status != null) {
            if (!attractionName.isEmpty() && slotTo.isPresent()) {
                // availabilitySlots.addAll(dataHandler.getAvailabilityForAttraction(attractionName, dayOfYear, slotFrom.get(), slotTo.get()));
            } else if (attractionName.isEmpty() && slotTo.isPresent()) {
                // availabilitySlots.addAll(dataHandler.getAvailabilityForAllAttractions(dayOfYear, slotFrom.get(), slotTo.get()));
            } else if (!attractionName.isEmpty()) {
                // availabilitySlots.addAll(dataHandler.getAvailabilityForSingleSlot(attractionName, dayOfYear, slotFrom.get()));
            } else {
                status = CheckAvailabilityStatus.CHECK_AVAILABILITY_UNKNOWN;
            }
        }
        responseObserver.onNext(AvailabilityResponse.newBuilder().setStatus(status).addAllSlot(availabilitySlots).build());
        responseObserver.onCompleted();;
    }

    @Override
    public void reserveAttraction(BookingRequest request, StreamObserver<ReservationResponse> responseObserver) {
        super.reserveAttraction(request, responseObserver);
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
