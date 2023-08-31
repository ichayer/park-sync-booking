package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

public class CsvFileProcessor {
    public static void processFile(String filename, AdminServiceGrpc.AdminServiceBlockingStub stub, Consumer<String[]> lineProcessor) throws IOException {
        if (filename == null) {
            throw new IllegalClientArgumentException("The action must provide a file using -DinPath=fileName");
        }

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        reader.readLine(); // Skip header
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(";");
            lineProcessor.accept(fields);
        }
    }
}
