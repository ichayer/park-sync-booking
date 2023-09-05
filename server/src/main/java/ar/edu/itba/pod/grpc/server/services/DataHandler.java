package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.Ticket;
import ar.edu.itba.pod.grpc.server.models.TicketType;

import java.util.*;

public class DataHandler {

    private final Map<String, Attraction> attractions;
    private final Map<UUID, Map<Integer, Ticket>> tickets;

    public DataHandler(Map<String, Attraction> attractions, Map<UUID, Map<Integer, Ticket>> tickets) {
        this.attractions = attractions;
        this.tickets = tickets;
    }

    public boolean addAttraction(String attractionName, Attraction attraction) {
        return this.attractions.putIfAbsent(attractionName, attraction) == null;
    }

    public boolean addTicket(UUID visitorId, int dayOfYear, TicketType ticketType) {
        this.tickets.putIfAbsent(visitorId, new HashMap<>());
        Map<Integer, Ticket> visitorTickets = tickets.get(visitorId);
        return visitorTickets.putIfAbsent(dayOfYear, new Ticket(visitorId, dayOfYear, ticketType)) == null;
    }

    public boolean containsAttraction(String attractionName) {
        return attractions.containsKey(attractionName);
    }


    // TODO: Waiting for Thomas :)
    public boolean setAttractionCapacityByDate(String attractionName, int dayOfYear, int capacity) {
        Attraction attraction = attractions.get(attractionName);
        return true;
    }

    public Collection<Attraction> getAttractions() {
        return Collections.unmodifiableCollection(this.attractions.values());
    }

}
