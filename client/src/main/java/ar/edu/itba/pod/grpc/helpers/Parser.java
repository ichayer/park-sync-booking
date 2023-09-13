package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;
import java.util.function.BiConsumer;

public class Parser {
    private static final Map<String, BiConsumer<String, Arguments.Builder>> OPTIONS = Map.ofEntries(
            Map.entry("-DserverAddress", (argValue, argBuilder) -> argBuilder.channel(ManagedChannelBuilder.forTarget(argValue).usePlaintext().build())),
            Map.entry("-Daction", (argValue, argBuilder) -> argBuilder.action(argValue)),
            Map.entry("-Dday", (argValue, argBuilder) -> argBuilder.dayOfYear(Integer.parseInt(argValue))),
            Map.entry("-Dride", (argValue, argBuilder) -> argBuilder.attractionName(argValue)),
            Map.entry("-Dvisitor", (argValue, argBuilder) -> argBuilder.visitorId(argValue)),
            Map.entry("-Dslot", (argValue, argBuilder) -> argBuilder.bookingSlot(argValue)),
            Map.entry("-DslotTo", (argValue, argBuilder) -> argBuilder.bookingSlotTo(argValue)),
            Map.entry("-DinPath", (argValue, argBuilder) -> argBuilder.filename(argValue)),
            Map.entry("-Dcapacity", (argValue, argBuilder) -> argBuilder.capacity(Integer.valueOf(argValue))),
            Map.entry("-DoutPath", (argValue, argBuilder) -> argBuilder.outFile(argValue)),
            Map.entry("-Dattraction", (argValue, argBuilder) -> argBuilder.attractionName(argValue))
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