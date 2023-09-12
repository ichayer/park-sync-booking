package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import io.grpc.ManagedChannel;

public class Arguments {
    private final ManagedChannel channel;
    private final String action;
    private final Integer dayOfYear;
    private final String attractionName;
    private final String visitorId;
    private final String bookingSlot;
    private final String bookingSlotTo;
    private final String filename;
    private final Integer capacity;
    private final String outFile;

    private Arguments(Builder builder) {
        this.channel = builder.channel;
        this.action = builder.action;
        this.dayOfYear = builder.dayOfYear;
        this.attractionName = builder.attractionName;
        this.visitorId = builder.visitorId;
        this.bookingSlot = builder.bookingSlot;
        this.bookingSlotTo = builder.bookingSlotTo;
        this.filename = builder.filename;
        this.capacity = builder.capacity;
        this.outFile = builder.outFile;

        if (channel == null || action == null) {
            throw new IllegalClientArgumentException("The parameters -DserverAddress and -Daction must be provided");
        }
    }

    public String getOutFile() {
        return outFile;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public String getAction() {
        return action;
    }

    public Integer getDayOfYear() {
        return dayOfYear;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public String getBookingSlot() {
        return bookingSlot;
    }

    public String getBookingSlotTo() {
        return bookingSlotTo;
    }

    public String getFilename() {
        return filename;
    }

    public Integer getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "channel=" + channel +
                ", action='" + action + '\'' +
                ", dayOfYear=" + dayOfYear +
                ", attractionName='" + attractionName + '\'' +
                ", visitorId='" + visitorId + '\'' +
                ", bookingSlot='" + bookingSlot + '\'' +
                ", bookingSlotTo='" + bookingSlotTo + '\'' +
                ", filename='" + filename + '\'' +
                ", capacity=" + capacity +
                ", outFile='" + outFile + '\'' +
                '}';
    }

    public static class Builder {
        private ManagedChannel channel;
        private String action;
        private Integer dayOfYear = null;
        private String attractionName = null;
        private String visitorId = null;
        private String bookingSlot = null;
        private String bookingSlotTo = null;
        private String filename = null;
        private Integer capacity = null;
        private String outFile = null;

        public Builder channel(ManagedChannel channel) {
            this.channel = channel;
            return this;
        }

        public Builder action(String action) {
            this.action = action.toUpperCase();
            return this;
        }

        public Builder outFile(String outFile) {
            this.outFile = outFile;
            return this;
        }

        public Builder dayOfYear(Integer dayOfYear) {
            this.dayOfYear = dayOfYear;
            return this;
        }

        public Builder attractionName(String rideName) {
            this.attractionName = rideName;
            return this;
        }

        public Builder visitorId(String visitorId) {
            this.visitorId = visitorId;
            return this;
        }

        public Builder bookingSlot(String bookingSlot) {
            this.bookingSlot = bookingSlot;
            return this;
        }

        public Builder bookingSlotTo(String bookingSlotTo) {
            this.bookingSlotTo = bookingSlotTo;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public Arguments build() {
            return new Arguments(this);
        }
    }
}
