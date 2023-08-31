package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.exceptions.IllegalClientArgumentException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class AdminClient {
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws InterruptedException {
        AdminArgumentParser arguments = null;
        try {
            arguments = new AdminArgumentParser(args);
        }catch (IllegalClientArgumentException e){
            System.out.println(e.getMessage());
        }
        finally{
            if(arguments!=null && arguments.getChannel() != null){
                arguments.getChannel().shutdown().awaitTermination(10, TimeUnit.SECONDS);
            }
        }
    }
}
