package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.server.models.*;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataHandler {

    private final Map<String, Attraction> attractions;
    private final Map<UUID, Map<Integer, Ticket>> tickets;

    public DataHandler() {
        this.attractions = new ConcurrentHashMap<>();
        this.tickets = new ConcurrentHashMap<>();
    }

    public DataHandler(Map<String, Attraction> attractions, Map<UUID, Map<Integer, Ticket>> tickets) {
        this.attractions = attractions;
        this.tickets = tickets;
    }

    public boolean addAttraction(String attractionName, Attraction attraction) {
        return this.attractions.putIfAbsent(attractionName, attraction) == null;
    }

    public boolean isSlotTimeValidForAttraction(String attractionName, int dayOfYear, LocalTime slotTime) {
        Attraction attraction = attractions.get(attractionName);
        return attraction != null && attraction.isSlotTimeValid(dayOfYear, slotTime);
    }

    public boolean containsAttraction(String attractionName) {
        return attractions.containsKey(attractionName);
    }

    public boolean setSlotCapacityForAttraction(String attractionName, int dayOfYear, int slotCapacity) {
        Attraction attraction = attractions.get(attractionName);
        return attraction != null && attraction.attemptToSetSlotCapacity(dayOfYear, slotCapacity);
    }

    public Collection<Attraction> getAttractions() {
        return Collections.unmodifiableCollection(this.attractions.values());
    }

    public boolean addTicket(UUID visitorId, int dayOfYear, TicketType ticketType) {
        Map<Integer, Ticket> visitorTickets = tickets.computeIfAbsent(visitorId, k -> new HashMap<>());
        if (visitorTickets.containsKey(dayOfYear)){
            return false;
        }
        visitorTickets.put(dayOfYear, new Ticket(visitorId, dayOfYear, ticketType));
        return true;
    }

    public boolean visitorHasTicketForDay(UUID visitorId, int dayOfYear) {
        return tickets.containsKey(visitorId) && tickets.get(visitorId).containsKey(dayOfYear);
    }

    public boolean visitorCanBookForDay(UUID visitorId, int dayOfYear, LocalTime slotTime) {
        return visitorHasTicketForDay(visitorId, dayOfYear) && tickets.get(visitorId).get(dayOfYear).canBook(slotTime);
    }

    public MakeReservationResult makeReservation(String attractionName, UUID visitorId, int dayOfYear, LocalTime slotTime) {
        Attraction attraction = attractions.get(attractionName);
        if (attraction == null)
            return MakeReservationResult.ATTRACTION_NOT_FOUND;

        Map<Integer, Ticket> visitorTickets = tickets.get(visitorId);
        Ticket ticket;
        if (visitorTickets == null || (ticket = visitorTickets.get(dayOfYear)) == null)
            return MakeReservationResult.MISSING_PASS;

        // TODO: Increment "bookings" count in the ticket in a thread-safe and transactional manner. THIS IS NOT OK.
        if (!ticket.attemptToBook(slotTime))
            return MakeReservationResult.MISSING_PASS;

        return attraction.makeReservation(ticket, slotTime);
    }
}
