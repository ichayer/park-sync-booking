package ar.edu.itba.pod.grpc.server.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public final class LocalTimeUtils {

    // Suppress default constructor for noninstantiability
    private LocalTimeUtils() {
        throw new AssertionError("Class is not instantiable");
    }

    private static final DateTimeFormatter slotTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static Optional<LocalTime> parseTimeOrEmpty(String time) {
        try {
            return Optional.of(LocalTime.parse(time, slotTimeFormatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static String formatTime(LocalTime time) {
        return slotTimeFormatter.format(time);
    }
}
