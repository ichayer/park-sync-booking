package ar.edu.itba.pod.grpc.errorHandling;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ApiStatus {

    UNKNOWN("UNKNOWN"),
    ATTRACTION_NOT_EXISTS("ATTRACTION_NOT_EXISTS"),
    INVALID_DAY("INVALID_DAY"),
    NEGATIVE_CAPACITY("NEGATIVE_CAPACITY"),
    CAPACITY_ALREADY_LOADED("CAPACITY_ALREADY_LOADED"),

    INVALID_SLOT("INVALID_SLOT"),
    RESERVATION_ALREADY_EXISTS("RESERVATION_ALREADY_EXISTS"),
    MISSING_PASS("MISSING_PASS"),
    NO_CAPACITY("NO_CAPACITY"),
    ALREADY_CONFIRMED("ALREADY_CONFIRMED"),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND");

    private final String messageCode;

    ApiStatus(String messageCode) {
        this.messageCode = messageCode;
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
}
