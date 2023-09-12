package ar.edu.itba.pod.grpc.server.notifications;

import ar.edu.itba.pod.grpc.server.exceptions.AlreadyRegisteredForNotificationsException;
import ar.edu.itba.pod.grpc.server.exceptions.NotRegisteredForNotificationsException;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.ConfirmedReservation;
import ar.edu.itba.pod.grpc.server.models.Reservation;
import ar.edu.itba.pod.grpc.server.utils.Constants;

import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of ReservationObserver that routes notifications to different NotificationStreamObserver instances
 * depending on the notification's attraction, visitor id, and day of year.
 */
public class NotificationRouter implements ReservationObserver {
    private final ConcurrentMap<Attraction, ConcurrentMap<UUID, NotificationStreamObserver>>[] streamsByDay;

    public NotificationRouter() {
        streamsByDay = new ConcurrentMap[Constants.DAYS_IN_YEAR];
        for (int i = 0; i < streamsByDay.length; i++)
            streamsByDay[i] = new ConcurrentHashMap<>();
    }

    @Override
    public void onSlotCapacitySet(Attraction attraction, int dayOfYear, int slotCapacity) {
        ConcurrentMap<Attraction, ConcurrentMap<UUID, NotificationStreamObserver>> attractionMap = streamsByDay[dayOfYear];
        ConcurrentMap<UUID, NotificationStreamObserver> idMap = attractionMap.get(attraction);

        if (idMap != null)
            idMap.forEach((vid, notif) -> notif.onSlotCapacitySet(attraction, dayOfYear, slotCapacity));
    }

    private NotificationStreamObserver getObserverIfExists(Reservation reservation) {
        int dayOfYear = reservation.getDayOfYear();
        Attraction attraction = reservation.getAttraction();

        ConcurrentMap<Attraction, ConcurrentMap<UUID, NotificationStreamObserver>> attractionMap = streamsByDay[dayOfYear];
        ConcurrentMap<UUID, NotificationStreamObserver> idMap = attractionMap.get(attraction);
        NotificationStreamObserver stream;

        if (idMap == null || (stream = idMap.get(reservation.getVisitorId())) == null)
            return null;

        return stream;
    }

    @Override
    public void onCreated(Reservation reservation, LocalTime slotTime, boolean isConfirmed) {
        NotificationStreamObserver stream = getObserverIfExists(reservation);
        if (stream != null)
            stream.onCreated(reservation, slotTime, isConfirmed);
    }

    @Override
    public void onConfirmed(ConfirmedReservation reservation) {
        NotificationStreamObserver stream = getObserverIfExists(reservation);
        if (stream != null)
            stream.onConfirmed(reservation);
    }

    @Override
    public void onRelocated(Reservation reservation, LocalTime prevSlotTime, LocalTime newSlotTime) {
        NotificationStreamObserver stream = getObserverIfExists(reservation);
        if (stream != null)
            stream.onRelocated(reservation, prevSlotTime, newSlotTime);
    }

    @Override
    public void onCancelled(Reservation reservation, LocalTime slotTime) {
        NotificationStreamObserver stream = getObserverIfExists(reservation);
        if (stream != null)
            stream.onCancelled(reservation, slotTime);
    }

    public void subscribe(NotificationStreamObserver observer, Attraction attraction, UUID visitorId, int dayOfYear) {
        ConcurrentMap<Attraction, ConcurrentMap<UUID, NotificationStreamObserver>> attractionMap = streamsByDay[dayOfYear];
        ConcurrentMap<UUID, NotificationStreamObserver> idMap = attractionMap.computeIfAbsent(attraction, k -> new ConcurrentHashMap<>());
        boolean success = idMap.putIfAbsent(visitorId, observer) == null;

        if (!success)
            throw new AlreadyRegisteredForNotificationsException();
    }

    public void unsubscribe(Attraction attraction, UUID visitorId, int dayOfYear) {
        ConcurrentMap<Attraction, ConcurrentMap<UUID, NotificationStreamObserver>> attractionMap = streamsByDay[dayOfYear];
        ConcurrentMap<UUID, NotificationStreamObserver> idMap;
        NotificationStreamObserver stream;
        if (attractionMap == null || (idMap = attractionMap.get(attraction)) == null || (stream = idMap.remove(visitorId)) == null)
            throw new NotRegisteredForNotificationsException();

        stream.onComplete();
    }
}
