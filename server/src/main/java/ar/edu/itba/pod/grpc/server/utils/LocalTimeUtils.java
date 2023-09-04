package ar.edu.itba.pod.grpc.server.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class LocalTimeUtils {

    public static Optional<LocalTime> parseTimeOrEmpty(String time) {
        Optional<LocalTime> parsedTime;
        try {
            parsedTime = Optional.of(LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
        } catch (DateTimeParseException e) {
            parsedTime = Optional.empty();
        }
        return parsedTime;
    }
}
