package ar.edu.itba.pod.grpc.server.notifications;

/**
 * An extension of NotificationObserver that includes a method to notify when there are no more notifications.
 */
public interface NotificationStream extends ReservationObserver {
    /**
     * Notifies this NotificationStream that no more notifications will be received.
     */
    void onComplete();
}
