package ar.edu.itba.pod.grpc.server.models;

import ar.edu.itba.pod.grpc.PassType;

import java.time.LocalTime;
import java.util.Optional;

public enum TicketType {

    FULL_DAY {
        @Override
        public boolean canBook(int bookings, LocalTime sloTime) {
            return bookings < 3;
        }
    },
    HALF_DAY {
        @Override
        public boolean canBook(int bookings, LocalTime sloTime) {
            return sloTime.isAfter(LocalTime.of(14,0));
        }
    },
    UNLIMITED {
        @Override
        public boolean canBook(int bookings, LocalTime sloTime) {
            return true;
        }
    };

    public static Optional<TicketType> fromPassType(PassType passType) {
        return switch (passType) {
            case PASS_TYPE_FULL_DAY -> Optional.of(TicketType.FULL_DAY);
            case PASS_TYPE_HALF_DAY -> Optional.of(TicketType.HALF_DAY);
            case PASS_TYPE_UNLIMITED -> Optional.of(TicketType.UNLIMITED);
            default -> Optional.empty();
        };
    }

    public static Optional<PassType> toPassType(TicketType ticket) {
        return switch (ticket) {
            case FULL_DAY -> Optional.of(PassType.PASS_TYPE_FULL_DAY);
            case HALF_DAY -> Optional.of(PassType.PASS_TYPE_HALF_DAY);
            case UNLIMITED -> Optional.of(PassType.PASS_TYPE_UNLIMITED);
            default -> Optional.empty();
        };
    }

    public abstract boolean canBook(int bookings, LocalTime sloTime);
}
