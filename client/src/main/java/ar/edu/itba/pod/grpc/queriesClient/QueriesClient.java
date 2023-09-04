package ar.edu.itba.pod.grpc.queriesClient;

import ar.edu.itba.pod.grpc.GenericClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueriesClient {
    private static final Logger logger = LoggerFactory.getLogger(QueriesClient.class);

    public static void main(String[] args) throws InterruptedException {
        GenericClient client = new GenericClient(new QueriesActionMapper());
        client.run(args);
    }
}
