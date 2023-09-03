package ar.edu.itba.pod.grpc.bookingClient.actions;

import ar.edu.itba.pod.grpc.Attraction;
import ar.edu.itba.pod.grpc.AttractionsResponse;
import ar.edu.itba.pod.grpc.BookingServiceGrpc;
import ar.edu.itba.pod.grpc.helpers.Arguments;
import ar.edu.itba.pod.grpc.interfaces.Action;
import com.google.protobuf.Empty;

import java.util.List;

public class AttractionsAction implements Action {

    private List<Attraction> attractionList;

    @Override
    public Action execute(Arguments arguments) {
        BookingServiceGrpc.BookingServiceBlockingStub client = BookingServiceGrpc.newBlockingStub(arguments.getChannel());

        AttractionsResponse response = client.getAttractions(Empty.getDefaultInstance());
        attractionList = response.getAttractionList();
        return this;
    }

    @Override
    public void showResults() {
        System.out.println("Atracciones del parque:");
        for (Attraction attraction : attractionList) {
            System.out.printf("Nombre: %s\n", attraction.getName());
            System.out.printf("Horario de Apertura: %s\n", attraction.getOpeningTime());
            System.out.printf("Horario de Cierre: %s\n", attraction.getClosingTime());
            System.out.println("----------------------------------");
        }
    }
}
