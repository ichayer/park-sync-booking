package ar.edu.itba.pod.grpc.server;

import ar.edu.itba.pod.grpc.server.handlers.AttractionHandler;
import ar.edu.itba.pod.grpc.server.notifications.NotificationRouter;
import ar.edu.itba.pod.grpc.server.services.AdminServiceImpl;
import ar.edu.itba.pod.grpc.server.services.BookingServiceImpl;
import ar.edu.itba.pod.grpc.server.services.ExceptionHandler;
import ar.edu.itba.pod.grpc.server.services.NotificationServiceImpl;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Server Starting ...");

        NotificationRouter notificationRouter = new NotificationRouter();
        AttractionHandler attractionHandler = new AttractionHandler(notificationRouter);

        int port = 50051;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .intercept(new ExceptionHandler())
                .addService(new AdminServiceImpl(attractionHandler))
                .addService(new BookingServiceImpl(attractionHandler))
                .addService(new NotificationServiceImpl(attractionHandler, notificationRouter))
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
