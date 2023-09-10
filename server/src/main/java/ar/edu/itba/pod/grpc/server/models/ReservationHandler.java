package ar.edu.itba.pod.grpc.server.models;

import ar.edu.itba.pod.grpc.server.exceptions.CapacityAlreadyDefinedException;
import ar.edu.itba.pod.grpc.server.exceptions.InvalidSlotException;
import ar.edu.itba.pod.grpc.server.exceptions.OutOfCapacityException;
import ar.edu.itba.pod.grpc.server.exceptions.ReservationAlreadyExistsException;
import ar.edu.itba.pod.grpc.server.results.DefineSlotCapacityResult;
import ar.edu.itba.pod.grpc.server.results.SuggestedCapacityResult;

import java.time.LocalTime;
import java.util.*;

/**
 * Manages the reservations for an attraction, for a specific day.
 */
public class ReservationHandler {

    // TODO: On reservation cancelled, decrement the amount of bookings in the Ticket instance.

    /**
     * The attraction for which this ReservationHandler manages reservations.
     */
    private final Attraction attraction;

    /**
     * The day of year for which this ReservationHandler manages reservations.
     */
    private final int dayOfYear;

    /**
     * The minute-of-day of the first slot.
     */
    private final int firstSlotMinuteOfDay;

    /**
     * The total amount of slots available for the day.
     */
    private final int slotCount;

    /**
     * The amount of people each slot may assign, or -1 if this has not been defined yet.
     */
    private int slotCapacity = -1;

    /**
     * Stores the confirmed set of visitors for each slot. The slots are stored ordered by time ascending.
     * Note: all elements in this array start as null and are created as needed.
     */
    private final Map<UUID, Reservation>[] slotConfirmedRequests;

    /**
     * Stores the pending reservation requests for each slot. Requests are added to this queue as they arrive, and
     * therefore are ordered chronologically.
     * Note: all elements in this array start as null and are created as needed.
     */
    private final LinkedHashMap<UUID, Reservation>[] slotPendingRequests;

    /**
     * Creates a new ReservationHandler for the given attraction and day of year.
     */
    public ReservationHandler(Attraction attraction, int dayOfYear) {
        this.attraction = Objects.requireNonNull(attraction);
        this.dayOfYear = dayOfYear;

        LocalTime openingTime = attraction.getOpeningTime();
        LocalTime closingTime = attraction.getClosingTime();
        int slotDuration = attraction.getSlotDuration();
        this.firstSlotMinuteOfDay = openingTime.getMinute() + openingTime.getHour() * 60;

        // slotCount is calculated as: slotCount = ceiling(totalMinutesOpen / slotDuration)
        int closingTimeMinuteOfDay = closingTime.getMinute() + closingTime.getHour() * 60;
        this.slotCount = (closingTimeMinuteOfDay - firstSlotMinuteOfDay + slotDuration - 1) / slotDuration;
        if (this.slotCount <= 0)
            throw new IllegalArgumentException("The attraction must have at least one time slot");

        this.slotConfirmedRequests = (Map<UUID, Reservation>[]) new Map[slotCount];
        this.slotPendingRequests = (LinkedHashMap<UUID, Reservation>[]) new LinkedHashMap[slotCount];
    }

    /**
     * Creates a new ReservationHandler for the given attraction and day of year, and includes the internal confirmed
     * and pending data structures.
     * THIS CONSTRUCTOR IS INTENDED ONLY FOR TESTING. Use the other constructor for everything else.
     */
    public ReservationHandler(Attraction attraction, int dayOfYear, int slotCapacity, Map<UUID, Reservation>[] slotConfirmedRequests, LinkedHashMap<UUID, Reservation>[] slotPendingRequests) {
        this.attraction = Objects.requireNonNull(attraction);
        this.dayOfYear = dayOfYear;
        this.slotCapacity = slotCapacity;

        LocalTime openingTime = attraction.getOpeningTime();
        LocalTime closingTime = attraction.getClosingTime();
        int slotDuration = attraction.getSlotDuration();
        this.firstSlotMinuteOfDay = openingTime.getMinute() + openingTime.getHour() * 60;

        // slotCount is calculated as: slotCount = ceiling(totalMinutesOpen / slotDuration)
        int closingTimeMinuteOfDay = closingTime.getMinute() + closingTime.getHour() * 60;
        this.slotCount = (closingTimeMinuteOfDay - firstSlotMinuteOfDay + slotDuration - 1) / (slotDuration);
        if (this.slotCount <= 0)
            throw new IllegalArgumentException("The attraction must have at least one time slot");

        this.slotConfirmedRequests = slotConfirmedRequests;
        this.slotPendingRequests = slotPendingRequests;
    }

    private Map<UUID, Reservation> getOrCreateSlotConfirmedRequests(int slotIndex) {
        Map<UUID, Reservation> confirmed = slotConfirmedRequests[slotIndex];
        if (confirmed == null)
            confirmed = slotConfirmedRequests[slotIndex] = new HashMap<>();

        return confirmed;
    }

    private LinkedHashMap<UUID, Reservation> getOrCreateSlotPendingRequests(int slotIndex) {
        LinkedHashMap<UUID, Reservation> pending = slotPendingRequests[slotIndex];
        if (pending == null)
            pending = slotPendingRequests[slotIndex] = new LinkedHashMap<>();

        return pending;
    }

    /**
     * Gets the index in the 'slots' array where the slot for a given time is, or -1 if there's no slot with that time.
     */
    private int getSlotIndex(LocalTime slotTime) {
        int slotDuration = attraction.getSlotDuration();
        int slotMinuteOfDay = slotTime.getMinute() + slotTime.getHour() * 60;
        int diff = slotMinuteOfDay - firstSlotMinuteOfDay;
        int slotIndex = diff / slotDuration;

        // Check that slotTime was the exact time at which the slot starts. Without this check, if a slot went from
        // 9:00 to 9:30 and a slotTime of 9:15 was specified, it would be taken as that slot.
        if (slotIndex * slotDuration != diff)
            return -1;

        return (slotIndex < 0 || slotIndex >= slotCount) ? -1 : slotIndex;
    }

    /**
     * Inverse of getSlotIndex().
     */
    private LocalTime getSlotTimeByIndex(int slotIndex) {
        int slotMinuteOfDay = firstSlotMinuteOfDay + slotIndex * attraction.getSlotDuration();
        return LocalTime.ofSecondOfDay(slotMinuteOfDay * 60L);
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getSlotCount() {
        return slotCount;
    }

    /**
     * Gets the slot capacity, or -1 if it hasn't been defined yet.
     */
    public int getSlotCapacity() {
        return slotCapacity;
    }

    /**
     * Returns true if the slot time is valid, false otherwise.
     */
    public boolean isSlotTimeValid(LocalTime slotTime) {
        return getSlotIndex(slotTime) >= 0;
    }

    /**
     * Sets the slot capacity, if it isn't already set.
     * @throws CapacityAlreadyDefinedException if slot capacity is already defined.
     */
    public synchronized DefineSlotCapacityResult defineSlotCapacity(int slotCapacity) {
        if (this.slotCapacity != -1)
            throw new CapacityAlreadyDefinedException();

        this.slotCapacity = slotCapacity;

        // Apply the reservation relocation algorithm to confirm the pending requests into the slots, or relocate.

        int bookingsConfirmed = 0;
        int bookingsRelocated = 0;
        int bookingsCancelled = 0;

        for (int slotIndex = 0; slotIndex < slotPendingRequests.length; slotIndex++) {
            LinkedHashMap<UUID, Reservation> requests = slotPendingRequests[slotIndex];
            if (requests == null || requests.isEmpty())
                continue;

            Map<UUID, Reservation> confirmed = getOrCreateSlotConfirmedRequests(slotIndex);

            // Dequeue the first N requests from the pending queue and confirm them (N = slotCapacity).
            Iterator<Reservation> iterator = requests.values().iterator();
            while (confirmed.size() < slotCapacity && iterator.hasNext()) {
                Reservation next = iterator.next();
                iterator.remove();
                next.setAsConfirmed();

                confirmed.put(next.getVisitorId(), next);
                bookingsConfirmed++;
                // TODO: Alert that the request was confirmed.
            }
        }

        // Attempt to relocate forward all pending requests, traversing by slot chronologically.
        for (int slotIndex = 0; slotIndex < slotPendingRequests.length; slotIndex++) {
            LinkedHashMap<UUID, Reservation> requests = slotPendingRequests[slotIndex];
            if (requests == null || requests.isEmpty())
                continue;

            int amountToRelocate = slotConfirmedRequests[slotIndex].size() + requests.size() - slotCapacity;
            Iterator<Reservation> requestsIterator = requests.values().iterator();
            for (int i = 0; i < amountToRelocate; i++) {
                Reservation visitorToRelocate = requestsIterator.next();
                requestsIterator.remove();

                // Implementation note: When a ticket is attempted to be moved but the destination time slot is invalid
                // due to the ticket type, no further time slots are attempted. If a new ticket type is implemented in
                // the future where the valid time slots aren't contiguous, this algorithm will have to be adapted.
                boolean relocated = false;
                int relocateSlotIndex = slotIndex + 1;
                while (!relocated && relocateSlotIndex < slotCount && visitorToRelocate.getTicket().getTicketType().isSlotTimeValid(getSlotTimeByIndex(relocateSlotIndex))) {
                    Map<UUID, Reservation> relocateSlotConfirmed = getOrCreateSlotConfirmedRequests(relocateSlotIndex);
                    LinkedHashMap<UUID, Reservation> relocateSlotPending = slotPendingRequests[relocateSlotIndex];
                    int relocateSlotTotal = relocateSlotConfirmed.size() + (relocateSlotPending == null ? 0 : relocateSlotPending.size());

                    if (relocateSlotTotal < slotCapacity) {
                        relocateSlotPending = getOrCreateSlotPendingRequests(relocateSlotIndex);
                        relocated = relocateSlotPending.putIfAbsent(visitorToRelocate.getVisitorId(), visitorToRelocate) == null;
                    }

                    if (!relocated)
                        relocateSlotIndex++;
                }

                if (relocated) {
                    // TODO: Alert that the request was relocated.
                    bookingsRelocated++;
                } else {
                    // TODO: Alert that the request was cancelled.
                    bookingsCancelled++;
                }
            }
        }

        return new DefineSlotCapacityResult(bookingsConfirmed, bookingsRelocated, bookingsCancelled);
    }

    /**
     * Attempts to make a reservation for a given visitor and time slot.
     * @return A Reservation
     * @throws InvalidSlotException, ReservationAlreadyExistsException, OutOfCapacityException,
     * ReservationAlreadyExistsException,
     */
    public synchronized Reservation makeReservation(Ticket ticket, LocalTime slotTime) {
        final int slotIndex = getSlotIndex(slotTime);
        if (slotIndex == -1)
            throw new InvalidSlotException();

        // The behavior of this method changes depending on whether slot capacities have been defined:
        // - If slot capacities have not been defined, the reservation is queued until they are.
        // - If slot capacities have been defined, the reservation is attempted immediately.

        if (slotCapacity == -1) {
            // Slot capacity has not been defined, queue the reservation.
            Reservation reservation = new Reservation(ticket, attraction, slotTime, false);
            if (getOrCreateSlotPendingRequests(slotIndex).putIfAbsent(reservation.getVisitorId(), reservation) != null)
                throw new ReservationAlreadyExistsException();

            // TODO: Notify new pending reservation created.
            return reservation;
        }

        // Slot capacity has been defined, attempt the reservation right now.
        final Map<UUID, Reservation> confirmed = getOrCreateSlotConfirmedRequests(slotIndex);
        if (confirmed.size() >= slotCapacity)
            throw new OutOfCapacityException();

        // Check if the reservation already exists as pending
        // TODO: Decide if the way to handle this side case is to raise an error or confirm the pending reservation.
        LinkedHashMap<UUID, Reservation> pendings = slotPendingRequests[slotIndex];
        if (pendings != null && pendings.containsKey(ticket.getVisitorId()))
            throw new ReservationAlreadyExistsException();

        // TODO: Discuss replacing with confirmed.computeIfAbsent. The issue with that function is that we can't differentiate the return value.
        Reservation reservation = new Reservation(ticket, attraction, slotTime, true);
        boolean success = confirmed.putIfAbsent(reservation.getVisitorId(), reservation) == null;
        if (!success)
            throw new ReservationAlreadyExistsException();

        // TODO: Decide if pending reservations are cancelled automatically when a slot fills up, or if the confirmation fails when the user attempts it.
        // If max capacity was reached for this slot, cancel all its pending reservations.
        if (confirmed.size() >= slotCapacity && pendings != null) {
            for (Reservation r : pendings.values()) {
                // TODO: Notify reservation cancelled.
            }
            pendings.clear();
        }

        //  TODO: Notify new confirmed reservation created.
        return reservation;
    }

    public synchronized void confirmReservation(UUID visitorId, LocalTime slotTime) {
        // TODO: Pretty up this method's interface (waiting for exception handling before doing this)
        int slotIndex = getSlotIndex(slotTime);
        if (slotIndex == -1)
            throw new IllegalStateException("Invalid slot");

        LinkedHashMap<UUID, Reservation> pendings = slotPendingRequests[slotIndex];
        Reservation reservation;
        if (pendings == null || (reservation = pendings.remove(visitorId)) == null)
            throw new IllegalArgumentException("Reservation not found");

        Map<UUID, Reservation> confirmed = getOrCreateSlotConfirmedRequests(slotIndex);
        boolean success = confirmed.putIfAbsent(reservation.getVisitorId(), reservation) == null;

        if (success) {
            reservation.setAsConfirmed();
            // TODO: Notify reservation cancelled? (due to internal server error, this should never happen)
        } else {
            // TODO: Notify reservation confirmed.
        }
    }

    public synchronized void cancelReservation(UUID visitorId, LocalTime slotTime) {
        // TODO: Pretty up this method's interface (waiting for exception handling before doing this)
        int slotIndex = getSlotIndex(slotTime);
        if (slotIndex == -1)
            throw new IllegalStateException("Invalid slot");

        LinkedHashMap<UUID, Reservation> pendings = slotPendingRequests[slotIndex];
        Reservation reservation = null;
        if (pendings == null || (reservation = pendings.remove(visitorId)) == null) {
            Map<UUID, Reservation> confirmed = slotConfirmedRequests[slotIndex];
            if (confirmed == null || (reservation = confirmed.remove(visitorId)) == null)
                throw new IllegalArgumentException("Reservation not found");
        }

        // TODO: Notify reservation cancelled.
    }

    /**
     * Computes the suggested slot capacity.
     * @return The suggested capacity as the maximum between all slots, and the slot with the said maximum capacity.
     * @throws IllegalStateException if slot capacity has already been decided.
     */
    public synchronized SuggestedCapacityResult getSuggestedCapacity() {
        if (slotCapacity != -1)
            throw new IllegalStateException("Slot capacity was already decided for this ReservationHandler");

        if (slotCount == 0)
            return SuggestedCapacityResult.EMPTY;

        // Find the slotIndex wih the maximum amount of pending reservations.
        int indexOfMax = 0;
        int maxPendingReservationCount = slotPendingRequests[0].size();
        for (int i = 1; i < slotPendingRequests.length; i++) {
            if (slotPendingRequests[i] != null && slotPendingRequests[i].size() > maxPendingReservationCount) {
                indexOfMax = i;
                maxPendingReservationCount = slotPendingRequests[i].size();
            }
        }

        if (indexOfMax == 0 && maxPendingReservationCount == 0)
            return SuggestedCapacityResult.EMPTY;

        return new SuggestedCapacityResult(maxPendingReservationCount, getSlotTimeByIndex(indexOfMax));
    }
}