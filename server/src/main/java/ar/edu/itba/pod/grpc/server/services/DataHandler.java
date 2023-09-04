package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.PassType;
import ar.edu.itba.pod.grpc.server.models.Attraction;

import java.time.LocalTime;
import java.util.*;

public class DataHandler {

    private final Map<String, Attraction> attractions;
    private final Map<String, Map<Integer, PassType>> tickets;

    public DataHandler(Map<String, Attraction> attractions, Map<String, Map<Integer, PassType>> tickets) {
        this.attractions = attractions;
        this.tickets = tickets;
    }

    public boolean addAttraction(String attractionName, Attraction attraction) {
        return this.attractions.putIfAbsent(attractionName, attraction) == null;
    }

    public boolean addTicket(String visitorId, int dayOfYear, PassType passType) {
        this.tickets.putIfAbsent(visitorId, new HashMap<>());
        Map<Integer, PassType> visitorTickets = tickets.get(visitorId);
        return visitorTickets.putIfAbsent(dayOfYear, passType) == null;
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
