package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.*;

/**
 * Manages the reservations for an attraction, for a specific day.
 */
public class ReservationsHandler {

    // TODO: Implement the slot allocation algorithm described in the document, point 1.3 (page 2).
    // TODO: Store the date each reservation was confirmed at, or find another way to fulfill query 4.2.

    /**
     * The duration of each slot, measured in minutes.
     */
    private final int slotDuration;

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
    private final Set<UUID>[] slotConfirmedRequests;

    /**
     * Stores the pending reservation requests for each slot. Requests are added to this queue as they arrive, and
     * therefore are ordered chronologically.
     * Note: all elements in this array start as null and are created as needed.
     */
    private final Queue<UUID>[] slotPendingRequests;

    public ReservationsHandler(int slotDuration, LocalTime openingTime, LocalTime closingTime) {
        this.slotDuration = slotDuration;
        this.firstSlotMinuteOfDay = openingTime.getMinute() + openingTime.getHour() * 60;

        // slotCount is calculated as: slotCount = ceiling(totalMinutesOpen / slotDuration)
        int closingTimeMinuteOfDay = closingTime.getMinute() + closingTime.getHour() * 60;
        this.slotCount = (closingTimeMinuteOfDay - firstSlotMinuteOfDay + slotDuration - 1) / (slotDuration);
        if (this.slotCount <= 0)
            throw new IllegalArgumentException("The attraction must have at least one time slot");

        this.slotConfirmedRequests = (Set<UUID>[]) new Set[slotCount];
        this.slotPendingRequests = (Queue<UUID>[]) new List[slotCount];
    }

    private Set<UUID> getOrCreateSlotConfirmedRequests(int slotIndex) {
        Set<UUID> confirmed = slotConfirmedRequests[slotIndex];
        if (confirmed == null)
            confirmed = slotConfirmedRequests[slotIndex] = new HashSet<>();

        return confirmed;
    }

    private Queue<UUID> getOrCreateSlotPendingRequests(int slotIndex) {
        Queue<UUID> pending = slotPendingRequests[slotIndex];
        if (pending == null)
            pending = slotPendingRequests[slotIndex] = new LinkedList<>();

        return pending;
    }

    /**
     * Gets the index in the 'slots' array where the slot for a given time is, or -1 if there's no slot with that time.
     */
    private int getSlotIndex(LocalTime slotTime) {
        int slotMinuteOfDay = slotTime.getMinute() + slotTime.getHour() * 60;
        int slotIndex = (slotMinuteOfDay - firstSlotMinuteOfDay) / slotDuration;

        return (slotIndex < 0 || slotIndex >= slotCount) ? -1 : slotIndex;
    }

    /**
     * Inverse of getSlotIndex().
     */
    private LocalTime getSlotTimeByIndex(int slotIndex) {
        int slotMinuteOfDay = firstSlotMinuteOfDay + slotIndex * slotDuration;
        return LocalTime.ofSecondOfDay(slotMinuteOfDay * 60L);
    }

    /**
     * Gets the duration of each slot, measured in minutes.
     */
    public int getSlotDuration() {
        return slotDuration;
    }

    /**
     * Gets the slot capacity, or -1 if it hasn't been defined yet.
     */
    public int getSlotCapacity() {
        return slotCapacity;
    }

    /**
     * Sets the slot capacity, if it isn't already set.
     * @throws IllegalStateException if slot capacity is already defined.
     */
    public synchronized void defineSlotCapacity(int slotCapacity) {
        if (this.slotCapacity != -1)
            throw new IllegalStateException("Cannot define slot capacity when already defined");

        this.slotCapacity = slotCapacity;

        // Apply the reservation relocation algorithm to confirm the pending requests into the slots, or relocate.

        for (int slotIndex = 0; slotIndex < slotPendingRequests.length; slotIndex++) {
            Queue<UUID> requests = slotPendingRequests[slotIndex];
            if (requests == null || requests.isEmpty())
                continue;

            Set<UUID> confirmed = getOrCreateSlotConfirmedRequests(slotIndex);

            // Dequeue the first N requests from the pending queue and confirm them. (N = slotCapacity)
            UUID next;
            while (confirmed.size() < slotCapacity && (next = requests.poll()) != null) {
                confirmed.add(next);
                // TODO: Alert that the request was confirmed.
            }
        }

        // Attempt to relocate forward all pending requests, traversing by slot chronologically.
        int relocateSlotIndex = 0;
        for (int slotIndex = 0; slotIndex < slotPendingRequests.length && relocateSlotIndex < slotCount; slotIndex++) {
            Queue<UUID> requests = slotPendingRequests[slotIndex];
            if (requests == null || requests.isEmpty())
                continue;

            relocateSlotIndex = Math.max(relocateSlotIndex, slotIndex + 1);

            int amountToRelocate = slotConfirmedRequests[slotIndex].size() + requests.size() - slotCapacity;
            for (int i = 0; i < amountToRelocate; i++) {
                UUID visitorToRelocate = requests.remove();
                boolean relocated = false;

                while (!relocated && relocateSlotIndex < slotCount) {
                    Set<UUID> otherSlotConfirmed = getOrCreateSlotConfirmedRequests(relocateSlotIndex);
                    Queue<UUID> otherSlotPending = slotPendingRequests[relocateSlotIndex];
                    int otherSlotTotal = otherSlotConfirmed.size() + (otherSlotPending == null ? 0 : otherSlotPending.size());

                    if (otherSlotTotal < slotCapacity) {
                        otherSlotPending = getOrCreateSlotPendingRequests(relocateSlotIndex);
                        otherSlotPending.add(visitorToRelocate);
                        relocated = true;
                    } else {
                        relocateSlotIndex++;
                    }
                }

                if (relocated) {
                    // TODO: Alert that the request was relocated.
                } else {
                    // TODO: Alert that the request was cancelled.
                }
            }
        }
    }

    /**
     * Attempts to make a reservation for a given visitor and time slot.
     * @return The result of the operation.
     * @throws IllegalArgumentException if the requested slot time isn't valid.
     */
    public synchronized MakeReservationResult makeReservation(UUID visitorId, LocalTime slotTime) {
        final int slotIndex = getSlotIndex(slotTime);
        if (slotIndex == -1)
            throw new IllegalArgumentException("Invalid slotTime");

        // The behavior of this method changes depending on whether slot capacities have been defined:
        // - If slot capacities have not been defined, the reservation is queued until they are.
        // - If slot capacities have been defined, the reservation is attempted immediately.

        if (slotCapacity == -1) {
            // Slot capacity has not been defined, queue the reservation.
            getOrCreateSlotPendingRequests(slotIndex).add(visitorId);
            return MakeReservationResult.QUEUED;
        }

        // Slot capacity has been defined, attempt the reservation right now.
        final Set<UUID> set = slotConfirmedRequests[slotIndex];
        if (set.size() >= slotCapacity)
            return MakeReservationResult.OUT_OF_CAPACITY;

        boolean success = slotConfirmedRequests[slotIndex].add(visitorId);
        return success ? MakeReservationResult.CONFIRMED : MakeReservationResult.ALREADY_EXISTS;
    }

    /**
     * Represents the possible results of a make reservation request.
     */
    public enum MakeReservationResult {
        QUEUED,
        CONFIRMED,
        ALREADY_EXISTS,
        OUT_OF_CAPACITY
    }

    /**
     * Computes the suggested slot capacity.
     * @return The suggested capacity as the maximum between all slots, and the slot with the said maximum capacity.
     * @throws IllegalStateException if slot capacity has already been decided.
     */
    public synchronized SuggestedCapacityResult getSuggestedCapacity() {
        if (slotCapacity != -1)
            throw new IllegalStateException("Slot capacity was already decided for this ReservationsHandler");

        if (slotCount == 0)
            return new SuggestedCapacityResult(0, null);

        // Find the slotIndex wih the maximum amount of pending reservations.
        int indexOfMax = 0;
        int maxPendingReservationCount = 0;
        for (int i = 1; i < slotPendingRequests.length; i++) {
            if (slotPendingRequests[i] != null && slotPendingRequests[i].size() > maxPendingReservationCount) {
                indexOfMax = i;
                maxPendingReservationCount = slotPendingRequests[i].size();
            }
        }

        return new SuggestedCapacityResult(maxPendingReservationCount, getSlotTimeByIndex(indexOfMax));
    }

    /**
     * Represents the result of a get suggested capacity query.
     * @param maxPendingReservationCount The maximum amount of pending reservations any given slot has, or 0 if there are no slots.
     * @param slotTime The slot's time, or null if there are no slots.
     */
    private record SuggestedCapacityResult(int maxPendingReservationCount, LocalTime slotTime) {
    }
}