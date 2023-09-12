package ar.edu.itba.pod.grpc.queriesClient.actions;

import ar.edu.itba.pod.grpc.DayOfYearRequest;
import ar.edu.itba.pod.grpc.QueryServiceGrpc;
import ar.edu.itba.pod.grpc.exceptions.IOClientFileError;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class QueriesAction implements Action {

    private String outPath;

    @Override
    public Action execute(Arguments arguments) {
        if (arguments.getDayOfYear() == null || arguments.getOutFile() == null) {
            throw new IllegalClientArgumentException("The capacity action must be provided a day and a file path" +
                    "with the arguments -Dday=day -DoutPath=output.txt");
        }
        DayOfYearRequest request = DayOfYearRequest.newBuilder()
                .setDayOfYear(arguments.getDayOfYear())
                .build();

        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(arguments.getChannel());
        outPath = arguments.getOutFile();
        sendServerMessage(request, stub);
        return this;
    }

    protected abstract void sendServerMessage(DayOfYearRequest request, QueryServiceGrpc.QueryServiceBlockingStub stub);

    @Override
    public void showResults() {
        PrintWriter writer = null;

        try (FileWriter fileWriter = new FileWriter(outPath)) {
            writer = new PrintWriter(fileWriter);
            writeToFile(writer);
        } catch (IOException e) {
            throw new IOClientFileError("Error al escribir en el archivo de salida " + outPath, e.getCause());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected abstract void writeToFile(PrintWriter writer);
}
