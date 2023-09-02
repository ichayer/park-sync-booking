package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import io.grpc.stub.StreamObserver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final Map<String, Attraction> attractions;
    private final Map<String, Map<LocalDate, PassType>> tickets;

    public AdminServiceImpl(Map<String, Attraction> attractionsMap, Map<String, Map<LocalDate, PassType>> ticketsMap) {
        this.attractions = attractionsMap;
        this.tickets = ticketsMap;
    }

    @Override
    public void addAttraction(AttractionRequest request, StreamObserver<BooleanResponse> responseObserver) {
        boolean success = false;
        String attractionName = request.getName();
        Optional<LocalTime> openTime = parseTimeOrNull(request.getHoursFrom());
        Optional<LocalTime> closeTime = parseTimeOrNull(request.getHoursTo());
        int slotGap = request.getSlotGap();

        if (openTime.isPresent() && closeTime.isPresent()) {
            success = isValidAttractionRequest(attractionName, openTime.get(), closeTime.get(), slotGap);

            if (success) {
                Attraction attraction = new Attraction(attractionName, openTime.get(), closeTime.get(), slotGap);
                attractions.put(attractionName, attraction);
            }
        }

        responseObserver.onNext(BooleanResponse.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(TicketRequest request, StreamObserver<BooleanResponse> responseObserver) {
        boolean success = false;
        Optional<LocalDate> date = parseDateOrNull(request.getDayOfYear());

        // TODO: Check if PassType.forNumber(request.getPassType().getNumber() != null) validation is necessary
        if (date.isPresent()) {
            tickets.putIfAbsent(request.getVisitorId(), new HashMap<>());
            Map<LocalDate, PassType> visitorTickets = tickets.get(request.getVisitorId());
            success = visitorTickets.putIfAbsent(date.get(), request.getPassType()) == null;
        }
        responseObserver.onNext(BooleanResponse.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addCapacity(CapacityRequest request, StreamObserver<CapacityResponse> responseObserver) {
        String attractionName = request.getAttractionName();
        Optional<LocalDate> date = parseDateOrNull(request.getDayOfYear());
        int capacity = request.getCapacity();

        if (attractions.containsKey(attractionName) && date.isPresent() && capacity > 0) {
            Attraction attraction = attractions.get(attractionName);
            attraction.setCapacityByDate(date.get(), capacity);
        }

        // TODO: Confirm, cancel or assign another attraction to the visitor
        int hardcodedValue = 0;
        String hardcodedMessage = "This is a message";
        responseObserver.onNext(CapacityResponse.newBuilder()
                .setCancelledBookings(hardcodedValue)
                .setConfirmedBookings(hardcodedValue)
                .setRelocatedBookings(hardcodedValue)
                .setResultMessage(hardcodedMessage)
                .build());
        responseObserver.onCompleted();
    }

    private boolean isValidAttractionRequest(String attractionName, LocalTime openTime, LocalTime closeTime, int slotGap) {

        if (attractionName.isEmpty() || attractions.containsKey(attractionName)) {
            return false;
        }

        if (slotGap <= 0 || slotGap > 60) {
            return false;
        }

        return openTime.isBefore(closeTime);
    }

    private Optional<LocalDate> parseDateOrNull(String date) {
        Optional<LocalDate> parsedDate;
        try {
            parsedDate = Optional.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        } catch (DateTimeParseException e) {
            parsedDate = Optional.empty();
        }
        return parsedDate;
    }

    private Optional<LocalTime> parseTimeOrNull(String time) {
        Optional<LocalTime> parsedTime;
        try {
            parsedTime = Optional.of(LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
        } catch (DateTimeParseException e) {
            parsedTime = Optional.empty();
        }
        return parsedTime;
    }
}
