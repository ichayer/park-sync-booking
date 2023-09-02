package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.adminClient.actions.AdminActionMapper;
import ar.edu.itba.pod.grpc.exceptions.IOClientFileError;
import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static ar.edu.itba.pod.grpc.AdminServiceGrpc.newBlockingStub;

public class AdminClient {
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);


    public static void main(String[] args) throws InterruptedException {
        AdminArguments arguments = null;
        try {
            arguments = AdminArguments.parse(args);
            AdminActionMapper actionMapper = new AdminActionMapper(arguments);
            actionMapper.getAction(arguments.getStringAction()).execute().showResults();
        } catch (IllegalClientArgumentException | IOClientFileError e) {
            //TODO: improve
            System.out.println(e.getMessage());
        } finally {
            if (arguments != null && arguments.getChannel() != null) {
                arguments.getChannel().shutdown().awaitTermination(10, TimeUnit.SECONDS);
            }
        }
    }
}
