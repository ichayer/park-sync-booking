package ar.edu.itba.pod.grpc.server;

import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.services.AdminServiceImpl;
import ar.edu.itba.pod.grpc.server.services.BookingServiceImpl;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Server Starting ...");

        AttractionHandler attractionHandler = new AttractionHandler();
        int port = 50051;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AdminServiceImpl(attractionHandler))
                .addService(new BookingServiceImpl(attractionHandler))
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }
}
