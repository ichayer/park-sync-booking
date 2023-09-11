package ar.edu.itba.pod.grpc.server.handlers;

import ar.edu.itba.pod.grpc.AvailabilitySlot;
import ar.edu.itba.pod.grpc.server.exceptions.AttractionAlreadyExistsException;
import ar.edu.itba.pod.grpc.server.exceptions.AttractionNotFoundException;
import ar.edu.itba.pod.grpc.server.exceptions.MissingPassException;
import ar.edu.itba.pod.grpc.server.exceptions.TicketAlreadyExistsException;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.Ticket;
import ar.edu.itba.pod.grpc.server.models.TicketType;
import ar.edu.itba.pod.grpc.server.notifications.ReservationObserver;
import ar.edu.itba.pod.grpc.server.results.AttractionAvailabilityResult;
import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.results.MakeReservationResult;
import ar.edu.itba.pod.grpc.server.utils.Constants;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AttractionHandler {

    private final ConcurrentMap<String, Attraction> attractions;
    private final ConcurrentMap<UUID, Ticket>[] ticketsByDay;
    private final ReservationObserver reservationObserver;

    public AttractionHandler(ReservationObserver reservationObserver) {
        this.attractions = new ConcurrentHashMap<>();
        this.ticketsByDay = (ConcurrentMap<UUID, Ticket>[]) new ConcurrentMap[Constants.DAYS_IN_YEAR];
        for (int i = 0; i < ticketsByDay.length; i++)
            ticketsByDay[i] = new ConcurrentHashMap<>();
        this.reservationObserver = reservationObserver;
    }

    /**
     * Creates a AttractionHandler with the given attraction and ticket maps.
     * THIS CONSTRUCTOR IS INTENDED ONLY FOR TESTING. Use the default constructor for everything else.
     */
    public AttractionHandler(ConcurrentMap<String, Attraction> attractions, ConcurrentMap<UUID, Ticket>[] ticketsByDay) {
        this.attractions = attractions;
        this.ticketsByDay = ticketsByDay;
        this.reservationObserver = null;
    }

    /**
     * Gets an attraction by name.
     * @throws AttractionNotFoundException If no attraction is found with that name.
     */
    public Attraction getAttraction(String attractionName) {
        Attraction attraction = this.attractions.get(attractionName);
        if (attraction == null)
            throw new AttractionNotFoundException();

        return attraction;
    }

    public void createAttraction(String attractionName, LocalTime openTime, LocalTime closeTime, int slotDuration) {
        Attraction attraction = new Attraction(attractionName, openTime, closeTime, slotDuration, reservationObserver);
        if(this.attractions.putIfAbsent(attractionName, attraction) != null){
            throw new AttractionAlreadyExistsException();
        }
    }

    public DefineSlotCapacityResult setSlotCapacityForAttraction(String attractionName, int dayOfYear, int slotCapacity) {
        Attraction attraction = attractions.get(attractionName);
        if (attraction == null)
            throw new AttractionNotFoundException();

        return attraction.trySetSlotCapacity(dayOfYear, slotCapacity);
    }

    public Collection<Attraction> getAttractions() {
        return Collections.unmodifiableCollection(this.attractions.values());
    }

    public void addTicket(UUID visitorId, int dayOfYear, TicketType ticketType) {
        ConcurrentMap<UUID, Ticket> visitorTickets = ticketsByDay[dayOfYear - 1];
        Ticket ticket = new Ticket(visitorId, dayOfYear, ticketType);
        if(visitorTickets.putIfAbsent(visitorId, ticket) != null){
            throw new TicketAlreadyExistsException();
        }
    }

    public MakeReservationResult makeReservation(String attractionName, UUID visitorId, int dayOfYear, LocalTime slotTime) {
        Attraction attraction = attractions.get(attractionName);
        if (attraction == null)
            throw new AttractionNotFoundException();

        Ticket ticket = ticketsByDay[dayOfYear - 1].get(visitorId);
        if (ticket == null)
            throw new MissingPassException();

        synchronized (ticket) {
            if (!ticket.canBook(slotTime))
                throw new MissingPassException();
            MakeReservationResult result = attraction.tryMakeReservation(ticket, slotTime);
            ticket.addBook(slotTime);
            return result;
        }
    }

    /**
     * Gets the availability for a given attraction, day of year and time slot.
     * @throws AttractionNotFoundException If no attraction is found with that name.
     */
    public Collection<AttractionAvailabilityResult> getAvailabilityForAttraction(String attractionName, int dayOfYear, LocalTime slotFrom, LocalTime slotTo) {
        Attraction attraction = attractions.get(attractionName);
        if (attraction == null)
            throw new AttractionNotFoundException();

        return attraction.getAvailabilityForAttraction(dayOfYear, slotFrom, slotTo);
    }
}
