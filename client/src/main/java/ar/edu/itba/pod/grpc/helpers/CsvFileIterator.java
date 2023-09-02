package ar.edu.itba.pod.grpc.helpers;

import ar.edu.itba.pod.grpc.exceptions.IOClientFileError;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;

import java.io.*;
import java.util.Iterator;

public class CsvFileIterator implements Iterator<String[]>, Closeable {
    private final BufferedReader reader;
    private String currentLine;

    public CsvFileIterator(String filename){
        if (filename == null) {
            throw new IllegalArgumentException("The filename cannot be null");
        }

        try {
            reader = new BufferedReader(new FileReader(filename));
            reader.readLine(); // Skip header
            currentLine = reader.readLine();
        } catch (FileNotFoundException e) {
            throw new IllegalClientArgumentException("The file " + filename + " was not found", e.getCause());
        } catch (IOException e) {
            throw new IOClientFileError(e.getMessage(), e.getCause());
        }
    }

    @Override
    public boolean hasNext() {
        return currentLine != null;
    }

    @Override
    public String[] next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more lines to read");
        }

        String[] fields = currentLine.split(";");

        try {
            currentLine = reader.readLine();
        } catch (IOException e) {
            throw new IOClientFileError(e.getMessage(), e.getCause());
        }

        return fields;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    @Override
    public void close(){
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new IOClientFileError(e.getMessage(), e.getCause());
            }
        }
    }
}
