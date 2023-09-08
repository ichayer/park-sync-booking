package ar.edu.itba.pod.grpc.adminClient;

import ar.edu.itba.pod.grpc.GenericClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminClient {
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);

    public static void main(String[] args) throws InterruptedException {
        logger.debug("Admin client started, mapping actions");
        GenericClient client = new GenericClient(new AdminActionMapper());
        logger.debug("Admin actions mapper");
        client.run(args);
    }
}
