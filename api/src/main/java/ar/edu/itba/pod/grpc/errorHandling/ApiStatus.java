package ar.edu.itba.pod.grpc.errorHandling;

import io.grpc.Status;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ApiStatus {

    UNKNOWN("UNKNOWN", Status.UNKNOWN),
    ATTRACTION_NOT_EXISTS("ATTRACTION_NOT_EXISTS", Status.NOT_FOUND),
    INVALID_DAY("INVALID_DAY", Status.INVALID_ARGUMENT),
    NEGATIVE_CAPACITY("NEGATIVE_CAPACITY", Status.INVALID_ARGUMENT),
    CAPACITY_ALREADY_LOADED("CAPACITY_ALREADY_LOADED", Status.INVALID_ARGUMENT),

    INVALID_SLOT("INVALID_SLOT", Status.INVALID_ARGUMENT),
    RESERVATION_ALREADY_EXISTS("RESERVATION_ALREADY_EXISTS", Status.INVALID_ARGUMENT),
    MISSING_PASS("MISSING_PASS", Status.INVALID_ARGUMENT),
    NO_CAPACITY("NO_CAPACITY", Status.INVALID_ARGUMENT),
    ALREADY_CONFIRMED("ALREADY_CONFIRMED", Status.INVALID_ARGUMENT),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", Status.NOT_FOUND);

    private final String messageCode;
    private final Status status;

    ApiStatus(String messageCode, Status status) {
        this.messageCode = messageCode;
        this.status = status;
    }

    public String getMessageCode() {
        return messageCode;
    }

    private static final ApiStatus[] VALUES = ApiStatus.values();

    private static final Map<String, ApiStatus> VALUES_BY_CODE = Arrays.stream(VALUES).collect(Collectors.toMap(r -> r.messageCode, r -> r));

    /**
     * Gets the ErrorStatus value by ordinal if it exists, or null otherwise.
     */
    public static ApiStatus fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static ApiStatus fromCode(String code) {
        return code == null ? null : VALUES_BY_CODE.get(code.trim().toLowerCase());
    }

    public Status getStatus() {
        return status;
    }
}
