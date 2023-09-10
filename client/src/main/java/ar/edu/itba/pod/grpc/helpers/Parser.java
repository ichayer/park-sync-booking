package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;
import java.util.function.BiConsumer;

public class Parser {
    private static final Map<String, BiConsumer<String, Arguments.Builder>> OPTIONS = Map.of(
            "-DserverAddress", (argValue, argBuilder) -> argBuilder.channel(ManagedChannelBuilder.forTarget(argValue).usePlaintext().build()) ,
            "-Daction", (argValue, argBuilder) -> argBuilder.action(argValue),
            "-Dday", (argValue, argBuilder) -> argBuilder.dayOfYear(Integer.parseInt(argValue)),
            "-Dride", (argValue, argBuilder) -> argBuilder.attractionName(argValue),
            "-Dvisitor", (argValue, argBuilder) -> argBuilder.visitorId(argValue),
            "-Dslot", (argValue, argBuilder) -> argBuilder.bookingSlot(argValue),
            "-DslotTo", (argValue, argBuilder) -> argBuilder.bookingSlotTo(argValue),
            "-DinPath", (argValue, argBuilder) -> argBuilder.filename(argValue),
            "-Dcapacity", (argValue, argBuilder) -> argBuilder.capacity(Integer.valueOf(argValue)),
            "-DoutPath", (argValue, argBuilder) -> argBuilder.outFile(argValue)
    );

    private static void invalidArgument(String arg, Arguments.Builder argBuilder) {
        throw new IllegalArgumentException("The argument " + arg + " is not valid");
    }


    public static Arguments parse(String[] args) {
        Arguments.Builder arguments = new Arguments.Builder();
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length != 2) {
                throw new IllegalClientArgumentException("Arguments must have the format -Dargument=value");
            }
            try {
                OPTIONS.getOrDefault(parts[0], Parser::invalidArgument).accept(parts[1], arguments);
            } catch (Exception e) {
                throw new IllegalClientArgumentException(e.getMessage());
            }
        }
        return arguments.build();
    }
}