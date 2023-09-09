package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.*;

/**
 * Manages the reservations for an attraction, for a specific day.
 */
public class ReservationHandler {

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
    private final Queue<Reservation>[] slotPendingRequests;

    public ReservationHandler(Attraction attraction, int dayOfYear) {
        this.attraction = Objects.requireNonNull(attraction);
        this.dayOfYear = dayOfYear;

        LocalTime openingTime = attraction.getOpeningTime();
        LocalTime closingTime = attraction.getClosingTime();
        int slotDuration = attraction.getSlotDuration();
        this.firstSlotMinuteOfDay = openingTime.getMinute() + openingTime.getHour() * 60;

        // slotCount is calculated as: slotCount = ceiling(totalMinutesOpen / slotDuration)
        int closingTimeMinuteOfDay = closingTime.getMinute() + closingTime.getHour() * 60;
        this.slotCount = (closingTimeMinuteOfDay - firstSlotMinuteOfDay + slotDuration - 1) / (slotDuration);
        if (this.slotCount <= 0)
            throw new IllegalArgumentException("The attraction must have at least one time slot");

        this.slotConfirmedRequests = (Map<UUID, Reservation>[]) new Map[slotCount];
        this.slotPendingRequests = (Queue<Reservation>[]) new Queue[slotCount];
    }

    private Map<UUID, Reservation> getOrCreateSlotConfirmedRequests(int slotIndex) {
        Map<UUID, Reservation> confirmed = slotConfirmedRequests[slotIndex];
        if (confirmed == null)
            confirmed = slotConfirmedRequests[slotIndex] = new HashMap<>();

        return confirmed;
    }

    private Queue<Reservation> getOrCreateSlotPendingRequests(int slotIndex) {
        Queue<Reservation> pending = slotPendingRequests[slotIndex];
        if (pending == null)
            pending = slotPendingRequests[slotIndex] = new LinkedList<>();

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
     * @throws IllegalStateException if slot capacity is already defined.
     */
    public synchronized DefineSlotCapacityResult defineSlotCapacity(int slotCapacity) {
        if (this.slotCapacity != -1)
            return DefineSlotCapacityResult.CAPACITY_ALREADY_SET;

        this.slotCapacity = slotCapacity;

        // Apply the reservation relocation algorithm to confirm the pending requests into the slots, or relocate.

        int bookingsConfirmed = 0;
        int bookingsRelocated = 0;
        int bookingsCancelled = 0;

        for (int slotIndex = 0; slotIndex < slotPendingRequests.length; slotIndex++) {
            Queue<Reservation> requests = slotPendingRequests[slotIndex];
            if (requests == null || requests.isEmpty())
                continue;

            Map<UUID, Reservation> confirmed = getOrCreateSlotConfirmedRequests(slotIndex);

            // Dequeue the first N requests from the pending queue and confirm them. (N = slotCapacity)
            Reservation next;
            while (confirmed.size() < slotCapacity && (next = requests.poll()) != null) {
                confirmed.put(next.getVisitorId(), next);
                bookingsConfirmed++;
                // TODO: Alert that the request was confirmed.
            }
        }

        // Attempt to relocate forward all pending requests, traversing by slot chronologically.
        int relocateSlotIndex = 0;
        for (int slotIndex = 0; slotIndex < slotPendingRequests.length && relocateSlotIndex < slotCount; slotIndex++) {
            Queue<Reservation> requests = slotPendingRequests[slotIndex];
            if (requests == null || requests.isEmpty())
                continue;

            relocateSlotIndex = Math.max(relocateSlotIndex, slotIndex + 1);

            // TODO: Check that the time a reservation was relocated to is valid for the ticket.
            int amountToRelocate = slotConfirmedRequests[slotIndex].size() + requests.size() - slotCapacity;
            for (int i = 0; i < amountToRelocate; i++) {
                Reservation visitorToRelocate = requests.remove();
                boolean relocated = false;

                while (!relocated && relocateSlotIndex < slotCount) {
                    Map<UUID, Reservation> relocateSlotConfirmed = getOrCreateSlotConfirmedRequests(relocateSlotIndex);
                    Queue<Reservation> relocateSlotPending = slotPendingRequests[relocateSlotIndex];
                    int relocateSlotTotal = relocateSlotConfirmed.size() + (relocateSlotPending == null ? 0 : relocateSlotPending.size());

                    if (relocateSlotTotal < slotCapacity) {
                        relocateSlotPending = getOrCreateSlotPendingRequests(relocateSlotIndex);
                        relocateSlotPending.add(visitorToRelocate);
                        relocated = true;
                    } else {
                        relocateSlotIndex++;
                    }
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

        return new DefineSlotCapacityResult(DefineSlotCapacityResult.Status.SUCCESS, bookingsConfirmed, bookingsRelocated, bookingsCancelled);
    }

    /**
     * Attempts to make a reservation for a given visitor and time slot.
     * @return A MakeReservationResult with the result of the operation. If the result is an error, the
     * <code>.reservation</code> value will be null.
     * @implNote To check for success or status, use <code>.status</code> instead of reading fields from the
     * <code>.reservation</code>. This will avoid race conditions.
     */
    public synchronized MakeReservationResult makeReservation(Ticket ticket, LocalTime slotTime) {
        final int slotIndex = getSlotIndex(slotTime);
        if (slotIndex == -1)
            return MakeReservationResult.INVALID_SLOT_TIME;

        // The behavior of this method changes depending on whether slot capacities have been defined:
        // - If slot capacities have not been defined, the reservation is queued until they are.
        // - If slot capacities have been defined, the reservation is attempted immediately.

        if (slotCapacity == -1) {
            // Slot capacity has not been defined, queue the reservation.
            Reservation reservation = new Reservation(ticket, attraction, slotTime, false);
            // TODO: Notify new pending reservation created.
            getOrCreateSlotPendingRequests(slotIndex).add(reservation);
            return new MakeReservationResult(MakeReservationResult.Status.QUEUED, reservation);
        }

        // Slot capacity has been defined, attempt the reservation right now.
        final Map<UUID, Reservation> confirmed = getOrCreateSlotConfirmedRequests(slotIndex);
        if (confirmed.size() >= slotCapacity)
            return MakeReservationResult.OUT_OF_CAPACITY;

        // TODO: Consider replacing with confirmed.computeIfAbsent
        Reservation reservation = new Reservation(ticket, attraction, slotTime, true);
        boolean success = confirmed.putIfAbsent(reservation.getVisitorId(), reservation) == null;
        if (success) {
            //  TODO: Notify new confirmed reservation created.
            return new MakeReservationResult(MakeReservationResult.Status.CONFIRMED, reservation);
        }

        return MakeReservationResult.ALREADY_EXISTS;
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