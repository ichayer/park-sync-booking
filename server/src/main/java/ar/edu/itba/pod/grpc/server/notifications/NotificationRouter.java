package ar.edu.itba.pod.grpc.server.notifications;

import ar.edu.itba.pod.grpc.server.exceptions.NotRegisteredForNotificationsException;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.Reservation;
import ar.edu.itba.pod.grpc.server.utils.Constants;

import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of ReservationObserver that routes notifications to different ReservationStream instances
 * depending on the notification's attraction, visitor id, and day of year.
 */
public class NotificationRouter implements ReservationObserver {
    private final ConcurrentMap<Attraction, Map<UUID, NotificationStream>>[] streamsByDay;

    public NotificationRouter() {
        streamsByDay = new ConcurrentMap[Constants.DAYS_IN_YEAR];
        for (int i = 0; i < streamsByDay.length; i++)
            streamsByDay[i] = new ConcurrentHashMap<>();
    }

    @Override
    public void onSlotCapacitySet(Attraction attraction, int dayOfYear, int slotCapacity) {

    }

    @Override
    public void onCreated(Reservation reservation, boolean isConfirmed) {

    }

    @Override
    public void onConfirmed(Reservation reservation) {

    }

    @Override
    public void onRelocated(Reservation reservation, LocalTime newSlotTime) {

    }

    @Override
    public void onCancelled(Reservation reservation) {

    }

    public void subscribe(NotificationStream handler, Attraction attraction, UUID visitorId, int dayOfYear) {

    }

    public void unsubscribe(Attraction attraction, UUID visitorId, int dayOfYear) {
        ConcurrentMap<Attraction, Map<UUID, NotificationStream>> attractionMap = streamsByDay[dayOfYear];
        Map<UUID, NotificationStream> idMap;
        if (attractionMap == null || (idMap = attractionMap.get(attraction)) == null)
            throw new NotRegisteredForNotificationsException();

        // TODO: Rethink synchronization issues where another notification might be reported after onComplete().
        NotificationStream stream;
        synchronized (idMap) {
             if ((stream = idMap.remove(visitorId)) == null)
                 throw new NotRegisteredForNotificationsException();
             stream.onComplete();
        }
    }
}
