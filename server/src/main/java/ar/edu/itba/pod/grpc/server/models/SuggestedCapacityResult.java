package ar.edu.itba.pod.grpc.server.models;

import java.time.LocalTime;

/**
 * Represents the result of a get suggested capacity query.
 *
 * @param maxPendingReservationCount The maximum amount of pending reservations any given slot has, or 0 if there are no slots.
 * @param slotTime                   The slot's time, or null if there are no slots.
 */
public record SuggestedCapacityResult(int maxPendingReservationCount, LocalTime slotTime) {
    public static final SuggestedCapacityResult EMPTY = new SuggestedCapacityResult(0, null);

    /**
     * Returns whether this SuggestedCapacityResult is empty. This occurs when there are no reservations.
     */
    public boolean isEmpty() {
        return slotTime == null;
    }
}