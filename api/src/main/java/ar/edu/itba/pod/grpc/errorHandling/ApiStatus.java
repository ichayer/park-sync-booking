package ar.edu.itba.pod.grpc.errorHandling;

import io.grpc.Status;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ApiStatus {

    UNKNOWN("UNKNOWN", Status.UNKNOWN),
    ATTRACTION_NOT_FOUND("ATTRACTION_NOT_FOUND", Status.NOT_FOUND),
    INVALID_DAY("INVALID_DAY", Status.INVALID_ARGUMENT),
    NEGATIVE_CAPACITY("NEGATIVE_CAPACITY", Status.INVALID_ARGUMENT),
    INVALID_DURATION("INVALID_DURATION", Status.INVALID_ARGUMENT),
    INVALID_OPENING_AND_CLOSING_TIME("INVALID_OPENING_AND_CLOSING_TIME", Status.INVALID_ARGUMENT),
    CAPACITY_ALREADY_DEFINED("CAPACITY_ALREADY_DEFINED", Status.INVALID_ARGUMENT),
    INVALID_SLOT("INVALID_SLOT", Status.INVALID_ARGUMENT),
    INVALID_VISITOR_ID("INVALID_VISITOR_ID", Status.INVALID_ARGUMENT),
    RESERVATION_ALREADY_EXISTS("RESERVATION_ALREADY_EXISTS", Status.INVALID_ARGUMENT),
    MISSING_PASS("MISSING_PASS", Status.INVALID_ARGUMENT),
    NO_CAPACITY("NO_CAPACITY", Status.INVALID_ARGUMENT),
    ALREADY_CONFIRMED("ALREADY_CONFIRMED", Status.INVALID_ARGUMENT),
    ATTRACTION_ALREADY_EXISTS("ATTRACTION_ALREADY_EXISTS", Status.INVALID_ARGUMENT),
    TICKET_ALREADY_EXISTS("TICKET_ALREADY_EXISTS", Status.INVALID_ARGUMENT),
    EMPTY_ATTRACTION("EMPTY_ATTRACTION", Status.INVALID_ARGUMENT),
    INVALID_TICKET_TYPE("INVALID_TICKET_TYPE", Status.INVALID_ARGUMENT),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", Status.NOT_FOUND),
    OUT_OF_CAPACITY("OUT_OF_CAPACITY", Status.NOT_FOUND),
    ALREADY_REGISTERED_FOR_NOTIFICATIONS("ALREADY_REGISTERED_FOR_NOTIFICATIONS", Status.RESOURCE_EXHAUSTED),
    NOT_REGISTERED_FOR_NOTIFICATIONS("NOT_REGISTERED_FOR_NOTIFICATIONS", Status.NOT_FOUND);

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

    public static ApiStatus fromCode(String code) {
        return code == null ? null : VALUES_BY_CODE.get(code.trim().toUpperCase());
    }

    public Status getStatus() {
        return status;
    }
}
