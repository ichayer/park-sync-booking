package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;
import java.util.function.BiConsumer;

public class AdminArguments {
    private static final Map<String, BiConsumer<String, AdminArguments>> OPTIONS = Map.of(
            "-DserverAddress", (argValue, parser) -> parser.channel = ManagedChannelBuilder.forTarget(argValue).usePlaintext().build(),
            "-Daction", (argValue, parser) -> parser.action = AdminClientAction.getAction(argValue.toUpperCase()),
            "-DinPath", (argValue, parser) -> parser.filename = argValue,
            "-Dride", (argValue, parser) -> parser.rideName = argValue,
            "-Dday", (argValue, parser) -> parser.dayOfYear = argValue,
            "-Dcapacity", (argValue, parser) -> parser.capacity = Integer.valueOf(argValue)
    );
    private ManagedChannel channel;
    private AdminClientAction action;
    private String filename;
    private String rideName;
    private String dayOfYear;
    private Integer capacity;

    public AdminArguments(String[] args) {
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length != 2) {
                throw new IllegalClientArgumentException("Arguments must have the format -Dargument=value");
            }
            try {
                OPTIONS.getOrDefault(parts[0], AdminArguments::InvalidArgument).accept(parts[1], this);
            } catch (Exception e) {
                throw new IllegalClientArgumentException(e.getMessage());
            }
        }

        if (channel == null || action == null) {
            throw new IllegalClientArgumentException("the parameters -DserverAddress and -Daction must be provided");
        }
    }

    private static void InvalidArgument(String arg, AdminArguments parser) {
        throw new IllegalClientArgumentException("The argument " + arg + "is not valid");
    }

    @Override
    public String toString() {
        return "AdminArgumentParser{" +
                "channel=" + channel +
                ", action=" + action +
                ", filename='" + filename + '\'' +
                ", rideName='" + rideName + '\'' +
                ", dayOfYear=" + dayOfYear +
                ", capacity=" + capacity +
                '}';
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public AdminClientAction getAction() {
        return action;
    }

    public String getFilename() {
        return filename;
    }

    public String getRideName() {
        return rideName;
    }

    public String getDayOfYear() {
        return dayOfYear;
    }

    public Integer getCapacity() {
        return capacity;
    }
}
