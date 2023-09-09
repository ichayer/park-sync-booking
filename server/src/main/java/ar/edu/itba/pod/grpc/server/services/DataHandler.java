package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.server.models.*;

import java.time.LocalTime;
import java.util.*;

public class DataHandler {

    private final Map<String, Attraction> attractions;
    private final Map<UUID, Ticket[]> tickets;
    private static final int DAYS_OF_THE_YEAR = 365;

    public DataHandler(Map<String, Attraction> attractions, Map<UUID, Ticket[]> tickets) {
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
        // TODO: Inefficient handling of tickets. A user might only visit a few days a year, there's no reason to make
        // this an array. Consider replacing for a map.
        // Issue: This code creates a new ticket array each time, whether it already exists or not
        this.tickets.putIfAbsent(visitorId, new Ticket[DAYS_OF_THE_YEAR]);
        Ticket[] visitorTickets = tickets.get(visitorId);
        Ticket ticket = visitorTickets[dayOfYear - 1];

        if (ticket == null || !ticket.getVisitorId().equals(visitorId)) {
            visitorTickets[dayOfYear - 1] = new Ticket(visitorId, dayOfYear, ticketType);
            return true;
        }

        return false;
    }

    public boolean visitorHasTicketForDay(UUID visitorId, int dayOfYear) {
        return tickets.containsKey(visitorId) && tickets.get(visitorId)[dayOfYear - 1] != null;
    }

    public boolean visitorCanBookForDay(UUID visitorId, int dayOfYear, LocalTime slotTime) {
        return visitorHasTicketForDay(visitorId, dayOfYear) && tickets.get(visitorId)[dayOfYear - 1].canBook(slotTime);
    }

    public MakeReservationResult makeReservation(String attractionName, UUID visitorId, int dayOfYear, LocalTime slotTime) {
        Attraction attraction = attractions.get(attractionName);
        if (attraction == null)
            return MakeReservationResult.ATTRACTION_NOT_FOUND;

        Ticket[] arr = tickets.get(visitorId);
        Ticket ticket;
        if (arr == null || (ticket = arr[dayOfYear]) == null)
            return MakeReservationResult.MISSING_PASS;

        // TODO: Increment "bookings" count in the ticket in a thread-safe and transactional manner. THIS IS NOT OK.
        if (!ticket.attemptToBook(slotTime))
            return MakeReservationResult.MISSING_PASS;

        return attraction.makeReservation(ticket, slotTime);
    }
}
