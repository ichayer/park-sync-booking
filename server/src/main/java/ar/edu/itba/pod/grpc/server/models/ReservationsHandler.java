package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;
import java.util.*;

/**
 * Manages the reservations for an attraction, for a specific day.
 */
public class ReservationsHandler {

    // TODO: This class currently cannot handle storing pending requests after slot capacity has been determined.
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
     * Stores the set of visitors for each slot. The slots are stored ordered by time ascending.
     */
    private final Set<UUID>[] slots;

    /**
     * Stores the reservation requests received before capacity has been defined. Once capacity is defined, the queue
     * is flushed and this variable set to null.
     */
    private Queue<ReservationRequest> pendingReservations = new LinkedList<>();

    private record ReservationRequest(UUID visitorId, int slotIndex) {
    }

    public ReservationsHandler(int slotDuration, LocalTime openingTime, LocalTime closingTime) {
        this.slotDuration = slotDuration;
        this.firstSlotMinuteOfDay = openingTime.getMinute() + openingTime.getHour() * 60;

        // slotCount is calculated as: slotCount = ceiling(totalMinutesOpen / slotDuration)
        int closingTimeMinuteOfDay = closingTime.getMinute() + closingTime.getHour() * 60;
        this.slotCount = (closingTimeMinuteOfDay - firstSlotMinuteOfDay + slotDuration - 1) / (slotDuration);
        if (this.slotCount <= 0)
            throw new IllegalArgumentException("The attraction must have at least one time slot");

        this.slots = (Set<UUID>[]) new Set[slotCount];
        for (int i = 0; i < slots.length; i++)
            this.slots[i] = new HashSet<>();
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

        // TODO: Apply pending reservations algorithm to flush the pendingReservations queue into the slots.

        pendingReservations = null;
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
            pendingReservations.add(new ReservationRequest(visitorId, slotIndex));
            return MakeReservationResult.QUEUED;
        }

        // Slot capacity has been defined, attempt the reservation right now.
        final Set<UUID> set = slots[slotIndex];
        if (set.size() >= slotCapacity)
            return MakeReservationResult.OUT_OF_CAPACITY;

        boolean success = slots[slotIndex].add(visitorId);
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

        // Count how many reservations there are for each slot.
        int[] pendingReservationCounts = new int[slotCount];
        for (ReservationRequest request : pendingReservations)
            pendingReservationCounts[request.slotIndex]++;

        // Find the slotIndex wih the maximum amount of pending reservations.
        int indexOfMax = 0;
        for (int i = 1; i < pendingReservationCounts.length; i++) {
            if (pendingReservationCounts[i] > pendingReservationCounts[indexOfMax])
                indexOfMax = i;
        }

        return new SuggestedCapacityResult(pendingReservationCounts[indexOfMax], getSlotTimeByIndex(indexOfMax));
    }

    /**
     * Represents the result of a get suggested capacity query.
     * @param maxPendingReservationCount The maximum amount of pending reservations any given slot has, or 0 if there are no slots.
     * @param slotTime The slot's time, or null if there are no slots.
     */
    private record SuggestedCapacityResult(int maxPendingReservationCount, LocalTime slotTime) {
    }
}
