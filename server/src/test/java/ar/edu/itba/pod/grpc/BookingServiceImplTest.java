package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.Ticket;
import ar.edu.itba.pod.grpc.server.services.DataHandler;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ar.edu.itba.pod.grpc.server.services.BookingServiceImpl;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest {
    private static final String ATTRACTION_NAME = "attractionName";
    private static final LocalTime TIME_FROM = LocalTime.of(10, 0);
    private static final LocalTime TIME_TO = LocalTime.of(18, 0);
    private static final int SLOT_GAP = 10;
    private static final Map<String, Attraction> attractions = new HashMap<>();
    private static final Map<UUID, Ticket[]> tickets = new HashMap<>();
    private static final DataHandler dataHandler = new DataHandler(attractions, tickets);
    private static final BookingServiceImpl bookingService = new BookingServiceImpl(dataHandler);
    @Mock
    private static StreamObserver<ReservationResponse> reservationResponseObserver;
    @Mock
    private static StreamObserver<GetAttractionsResponse> attractionResponseObserver;

    @AfterEach
    public void setUp() {
        attractions.clear();
        tickets.clear();
    }

    // https://stackoverflow.com/questions/49871975/how-to-test-and-mock-a-grpc-service-written-in-java-using-mockito
    @Test
    public void testGetAttractionsWithOneAttraction() {
        attractionResponseObserver = Mockito.mock(StreamObserver.class);

        Attraction attraction = Mockito.mock(Attraction.class);
        Mockito.when(attraction.getName()).thenReturn(ATTRACTION_NAME);
        Mockito.when(attraction.getOpeningTime()).thenReturn(TIME_FROM);
        Mockito.when(attraction.getClosingTime()).thenReturn(TIME_TO);

        attractions.put(ATTRACTION_NAME, attraction);

        bookingService.getAttractions(Empty.newBuilder().build(), attractionResponseObserver);

        // Capture onNext argument for examination
        ArgumentCaptor<GetAttractionsResponse> responseCaptor = ArgumentCaptor.forClass(GetAttractionsResponse.class);
        Mockito.verify(attractionResponseObserver).onNext(responseCaptor.capture());
        GetAttractionsResponse capturedResponse = responseCaptor.getValue();

        Assert.assertEquals(1, capturedResponse.getAttractionList().size());
    }
}
