package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.Ticket;
import ar.edu.itba.pod.grpc.server.models.TicketType;

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

    public boolean addTicket(UUID visitorId, int dayOfYear, TicketType ticketType) {
        this.tickets.putIfAbsent(visitorId, new Ticket[DAYS_OF_THE_YEAR]);
        Ticket[] visitorTickets = tickets.get(visitorId);
        Ticket ticket = visitorTickets[dayOfYear - 1];

        if (ticket == null || ticket.getVisitorId().equals(visitorId)) {
            visitorTickets[dayOfYear - 1] = new Ticket(visitorId, dayOfYear, ticketType);
            return true;
        }

        return false;
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

}
