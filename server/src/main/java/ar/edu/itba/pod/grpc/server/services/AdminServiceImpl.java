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

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final Map<String, Attraction> attractions;
    private final Map<String, Map<LocalDate, PassType>> tickets;

    public AdminServiceImpl(Map<String, Attraction> attractionsMap, Map<String, Map<LocalDate, PassType>> ticketsMap) {
        this.attractions = attractionsMap;
        this.tickets = ticketsMap;
    }

    @Override
    public void addAttraction(AttractionRequest request, StreamObserver<BooleanResponse> responseObserver) {
        String attractionName = request.getName();
        LocalTime openTime = parseTimeOrNull(request.getHoursFrom());
        LocalTime closeTime = parseTimeOrNull(request.getHoursTo());
        int slotGap = request.getSlotGap();

        boolean isValid = isValidAttractionRequest(attractionName, openTime, closeTime, slotGap);

        if (isValid) {
            Attraction attraction = new Attraction(attractionName, openTime, closeTime, slotGap);
            attractions.put(attractionName, attraction);
        }

        responseObserver.onNext(BooleanResponse.newBuilder().setSuccess(isValid).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addTicket(TicketRequest request, StreamObserver<BooleanResponse> responseObserver) {
        boolean success = false;
        LocalDate date = parseDateOrNull(request.getDayOfYear());

        // TODO: Check if PassType.forNumber(request.getPassType().getNumber() != null) validation is necessary
        if (date != null) {

            if (!tickets.containsKey(request.getVisitorId())) {
                tickets.put(request.getVisitorId(), new HashMap<>());
            }

            Map<LocalDate, PassType> visitorTickets = tickets.get(request.getVisitorId());

            if (!visitorTickets.containsKey(date)) {
                tickets.get(request.getVisitorId()).put(date, request.getPassType());
                success = true;
            }
        }
        responseObserver.onNext(BooleanResponse.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addCapacity(CapacityRequest request, StreamObserver<CapacityResponse> responseObserver) {
        super.addCapacity(request, responseObserver);
    }

    private boolean isValidAttractionRequest(String attractionName, LocalTime openTime, LocalTime closeTime, int slotGap) {

        if (attractionName.isEmpty() || attractions.containsKey(attractionName)) {
            return false;
        }

        if (slotGap <= 0 || slotGap > 60) {
            return false;
        }

        return openTime != null && closeTime != null && openTime.isBefore(closeTime);
    }

    private LocalDate parseDateOrNull(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalTime parseTimeOrNull(String time) {
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
