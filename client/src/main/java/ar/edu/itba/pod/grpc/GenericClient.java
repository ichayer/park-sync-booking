package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.exceptions.IOClientFileError;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import ar.edu.itba.pod.grpc.exceptions.ServerErrorReceived;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.helpers.Parser;
import ar.edu.itba.pod.grpc.interfaces.ActionMapper;

import java.util.concurrent.TimeUnit;

public class GenericClient {

    private final ActionMapper actionMapper;

    public GenericClient(ActionMapper actionMapper){
        this.actionMapper = actionMapper;
    }

    public void run(String[] args) throws InterruptedException{
        Arguments arguments = null;
        try {
            arguments = Parser.parse(args);
            actionMapper.getAction(arguments.getAction()).execute(arguments).showResults();
        } catch (IllegalClientArgumentException | IOClientFileError | ServerErrorReceived e) {
            System.out.println("Client error: " + e.getMessage());
        } catch(Exception e) {
            System.out.println("Unknown error: " + e.getMessage());
        }
        finally {
            if (arguments != null && arguments.getChannel() != null) {
                arguments.getChannel().shutdown().awaitTermination(10, TimeUnit.SECONDS);
            }
        }
    }
}