package ar.edu.itba.pod.test;

import ar.edu.itba.pod.grpc.AvailabilityRequest;
import ar.edu.itba.pod.grpc.AvailabilityResponse;
import ar.edu.itba.pod.grpc.BookingRequest;
import ar.edu.itba.pod.grpc.GetAttractionsResponse;
import ar.edu.itba.pod.grpc.server.exceptions.*;
import ar.edu.itba.pod.grpc.server.handlers.AttractionHandler;
import ar.edu.itba.pod.grpc.server.handlers.ReservationHandler;
import ar.edu.itba.pod.grpc.server.models.*;
import ar.edu.itba.pod.grpc.server.notifications.ReservationObserver;
import ar.edu.itba.pod.grpc.server.services.BookingServiceImpl;
import ar.edu.itba.pod.grpc.server.utils.ParseUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest {
    private static final String ATTRACTION_NAME = "attractionName";
    private static final String ANOTHER_ATTRACTION_NAME = "anotherAttractionName";
    private static final String NON_EXISTING_ATTRACTION_NAME = "nonExistingAttractionName";
    private static final UUID VISITOR_ID = UUID.randomUUID();
    private static final String TIME_FROM_STRING = "10:00";
    private static final String TIME_TO_STRING = "18:00";
    private static final LocalTime TIME_FROM_LOCAL_TIME = LocalTime.parse(TIME_FROM_STRING);
    private static final LocalTime TIME_TO_LOCAL_TIME = LocalTime.parse(TIME_TO_STRING);
    private static final int SLOT_DURATION_MINUTES = 30;
    private static final int TOTAL_SLOTS = (TIME_TO_LOCAL_TIME.toSecondOfDay() - TIME_FROM_LOCAL_TIME.toSecondOfDay()) / (SLOT_DURATION_MINUTES * 60);
    private static final int INVALID_DAY_OF_YEAR = 0;
    private static final int OTHER_INVALID_DAY_OF_YEAR = 366;
    private static final int VALID_DAY_OF_YEAR = 200;
    private static final int SLOT_CAPACITY = 6;
    private static final TicketType TICKET_TYPE_FULL_DAY = TicketType.FULL_DAY;
    private static final TicketType TICKET_TYPE_HALF_DAY = TicketType.HALF_DAY;
    private static final int MAX_BOOKINGS_FOR_FULL_DAY = 3;
    private static final ConcurrentMap<String, Attraction> attractions = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, Ticket>[] ticketsByDay = TestUtils.generateTicketsByDayMaps();
    private final AttractionHandler attractionHandler = new AttractionHandler(attractions, ticketsByDay);
    private final BookingServiceImpl bookingService = new BookingServiceImpl(attractionHandler);
    @Mock
    private static StreamObserver<AvailabilityResponse> availabilityResponseObserver;
    @Mock
    private static StreamObserver<GetAttractionsResponse> attractionResponseObserver;

    @Before
    public void setUp() {
        attractions.clear();
        for (int i = 0; i < ticketsByDay.length; i++)
            ticketsByDay[i].clear();
    }

    // https://stackoverflow.com/questions/49871975/how-to-test-and-mock-a-grpc-service-written-in-java-using-mockito
    @Test
    public void testGetAttractionsWithOneAttraction() {
        attractionResponseObserver = Mockito.mock(StreamObserver.class);

        Attraction attraction = Mockito.mock(Attraction.class);
        Mockito.when(attraction.getName()).thenReturn(ATTRACTION_NAME);
        Mockito.when(attraction.getOpeningTime()).thenReturn(TIME_FROM_LOCAL_TIME);
        Mockito.when(attraction.getClosingTime()).thenReturn(TIME_TO_LOCAL_TIME);

        attractions.put(ATTRACTION_NAME, attraction);

        bookingService.getAttractions(Empty.newBuilder().build(), attractionResponseObserver);

        // Capture onNext argument for examination
        ArgumentCaptor<GetAttractionsResponse> responseCaptor = ArgumentCaptor.forClass(GetAttractionsResponse.class);
        Mockito.verify(attractionResponseObserver).onNext(responseCaptor.capture());
        GetAttractionsResponse capturedResponse = responseCaptor.getValue();

        assertEquals(1, capturedResponse.getAttractionList().size());
    }

    @Test
    public void testGetAttractionsWithNoAttraction() {
        attractionResponseObserver = Mockito.mock(StreamObserver.class);

        bookingService.getAttractions(Empty.newBuilder().build(), attractionResponseObserver);

        ArgumentCaptor<GetAttractionsResponse> responseCaptor = ArgumentCaptor.forClass(GetAttractionsResponse.class);
        Mockito.verify(attractionResponseObserver).onNext(responseCaptor.capture());
        GetAttractionsResponse capturedResponse = responseCaptor.getValue();

        assertEquals(0, capturedResponse.getAttractionList().size());
    }

    @Test
    public void testCheckAttractionAvailabilityFailureMoreThan365Days() {
        assertThrows(InvalidDayException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setDayOfYear(OTHER_INVALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_FROM_STRING)
                            .setSlotTo(TIME_TO_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureLessThan1Day() {
        assertThrows(InvalidDayException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setDayOfYear(INVALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_FROM_STRING)
                            .setSlotTo(TIME_TO_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureInvalidSlotRange() {
        assertThrows(InvalidSlotException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_TO_STRING)
                            .setSlotTo(TIME_FROM_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureEmptyAttractionNameAndSlotTo() {
        assertThrows(CheckAvailabilityInvalidArgumentException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setAttractionName("")
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_TO_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureBlankAttractionNameAndSlotTo() {
        assertThrows(CheckAvailabilityInvalidArgumentException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setAttractionName("  ")
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_TO_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureNoAttractionNameAndSlotTo() {
        assertThrows(CheckAvailabilityInvalidArgumentException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_TO_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureNoSlotFrom() {
        assertThrows(InvalidSlotException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setAttractionName(ATTRACTION_NAME)
                            .setSlotTo(TIME_TO_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAttractionAvailabilityFailureNoDayOfYear() {
        assertThrows(InvalidDayException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setSlotTo(TIME_TO_STRING)
                            .setSlotFrom(TIME_FROM_STRING)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAvailabilityFailureForNonExistingAttraction() {
        assertThrows(AttractionNotFoundException.class, () -> {
            bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlotFrom(TIME_FROM_STRING)
                            .setSlotTo(TIME_TO_STRING)
                            .setAttractionName(NON_EXISTING_ATTRACTION_NAME)
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testCheckAvailabilityForNoAttractions() {
        availabilityResponseObserver = Mockito.mock(StreamObserver.class);
        bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlotFrom(TIME_FROM_STRING)
                        .setSlotTo(TIME_TO_STRING)
                        .build(),
                availabilityResponseObserver);

        ArgumentCaptor<AvailabilityResponse> responseCaptor = ArgumentCaptor.forClass(AvailabilityResponse.class);
        Mockito.verify(availabilityResponseObserver).onNext(responseCaptor.capture());
        AvailabilityResponse capturedResponse = responseCaptor.getValue();
        assertEquals(0, capturedResponse.getSlotList().size());
    }

    @Test
    public void testCheckAvailabilityForSpecificAttractionNoLimit() {
        Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction attractionSpy = Mockito.spy(attraction);
        attractions.put(ATTRACTION_NAME, attractionSpy);
        ReservationHandler reservationHandler = attraction.getReservationHandler(VALID_DAY_OF_YEAR);
        ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
        reservationHandlerSpy.defineSlotCapacity(SLOT_CAPACITY);
        attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

        bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                        .setAttractionName(ATTRACTION_NAME)
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlotFrom(TIME_FROM_STRING)
                        .build(),
                availabilityResponseObserver);

        ArgumentCaptor<AvailabilityResponse> responseCaptor = ArgumentCaptor.forClass(AvailabilityResponse.class);
        Mockito.verify(availabilityResponseObserver).onNext(responseCaptor.capture());
        AvailabilityResponse capturedResponse = responseCaptor.getValue();
        assertEquals(1, capturedResponse.getSlotList().size());
        assertEquals(SLOT_CAPACITY, capturedResponse.getSlot(0).getSlotCapacity());
        assertEquals(ATTRACTION_NAME, capturedResponse.getSlot(0).getAttractionName());
        assertEquals(0, capturedResponse.getSlot(0).getBookingsConfirmed());
        assertEquals(0, capturedResponse.getSlot(0).getBookingsPending());
        assertEquals(TIME_FROM_LOCAL_TIME.toString(), capturedResponse.getSlot(0).getSlot());
    }

    @Test
    public void testCheckAvailabilityForSpecificAttractionWithLimit() {
        Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction attractionSpy = Mockito.spy(attraction);
        attractions.put(ATTRACTION_NAME, attractionSpy);
        ReservationHandler reservationHandler = attraction.getReservationHandler(VALID_DAY_OF_YEAR);
        ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
        reservationHandlerSpy.defineSlotCapacity(SLOT_CAPACITY);
        attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

        int limit = 5;
        bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                        .setAttractionName(ATTRACTION_NAME)
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlotFrom(TIME_FROM_STRING)
                        .setSlotTo(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME.plusMinutes((long) limit * SLOT_DURATION_MINUTES)))
                        .build(),
                availabilityResponseObserver);

        ArgumentCaptor<AvailabilityResponse> responseCaptor = ArgumentCaptor.forClass(AvailabilityResponse.class);
        Mockito.verify(availabilityResponseObserver).onNext(responseCaptor.capture());
        AvailabilityResponse capturedResponse = responseCaptor.getValue();
        assertEquals(limit + 1, capturedResponse.getSlotList().size());
        for (int i = 0; i < limit + 1; i++) {
            assertEquals(SLOT_CAPACITY, capturedResponse.getSlot(i).getSlotCapacity());
            assertEquals(ATTRACTION_NAME, capturedResponse.getSlot(i).getAttractionName());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsConfirmed());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsPending());
            assertEquals(TIME_FROM_LOCAL_TIME.plusMinutes((long) i * SLOT_DURATION_MINUTES).toString(), capturedResponse.getSlot(i).getSlot());
        }
    }

    @Test
    public void testCheckAvailabilityForAllAttractions() {
        Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction attractionSpy = Mockito.spy(attraction);
        attractions.put(ATTRACTION_NAME, attractionSpy);
        ReservationHandler reservationHandler = attraction.getReservationHandler(VALID_DAY_OF_YEAR);
        ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
        reservationHandlerSpy.defineSlotCapacity(SLOT_CAPACITY);
        attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

        Attraction anotherAttraction = new Attraction(ANOTHER_ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction anotherAttractionSpy = Mockito.spy(anotherAttraction);
        attractions.put(ANOTHER_ATTRACTION_NAME, anotherAttractionSpy);
        ReservationHandler anotherReservationHandler = anotherAttraction.getReservationHandler(VALID_DAY_OF_YEAR);
        ReservationHandler anotherReservationHandlerSpy = Mockito.spy(anotherReservationHandler);
        anotherReservationHandlerSpy.defineSlotCapacity(SLOT_CAPACITY);
        anotherAttractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, anotherReservationHandlerSpy);

        bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlotFrom(TIME_FROM_STRING)
                        .setSlotTo(ParseUtils.formatTime(TIME_TO_LOCAL_TIME.minusMinutes(SLOT_DURATION_MINUTES)))
                        .build(),
                availabilityResponseObserver);

        ArgumentCaptor<AvailabilityResponse> responseCaptor = ArgumentCaptor.forClass(AvailabilityResponse.class);
        Mockito.verify(availabilityResponseObserver).onNext(responseCaptor.capture());
        AvailabilityResponse capturedResponse = responseCaptor.getValue();
        assertEquals(2 * TOTAL_SLOTS, capturedResponse.getSlotList().size());
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            assertEquals(SLOT_CAPACITY, capturedResponse.getSlot(i).getSlotCapacity());
            assertEquals(ANOTHER_ATTRACTION_NAME, capturedResponse.getSlot(i).getAttractionName());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsConfirmed());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsPending());
            assertEquals(TIME_FROM_LOCAL_TIME.plusMinutes((long) i * SLOT_DURATION_MINUTES).toString(), capturedResponse.getSlot(i).getSlot());
        }
        for (int i = TOTAL_SLOTS; i < 2 * TOTAL_SLOTS; i++) {
            assertEquals(SLOT_CAPACITY, capturedResponse.getSlot(i).getSlotCapacity());
            assertEquals(ATTRACTION_NAME, capturedResponse.getSlot(i).getAttractionName());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsConfirmed());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsPending());
            assertEquals(TIME_FROM_LOCAL_TIME.plusMinutes((long) (i - TOTAL_SLOTS) * SLOT_DURATION_MINUTES).toString(), capturedResponse.getSlot(i).getSlot());
        }
    }

    @Test
    public void testCheckAvailabilityConfirmed() {
        Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction attractionSpy = Mockito.spy(attraction);
        attractions.put(ATTRACTION_NAME, attractionSpy);

        Map<UUID, ar.edu.itba.pod.grpc.server.models.ConfirmedReservation>[] confirmedReservations = new Map[TOTAL_SLOTS];

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            confirmedReservations[i] = new ConcurrentHashMap<>();
            confirmedReservations[i].put(VISITOR_ID, Mockito.mock(ar.edu.itba.pod.grpc.server.models.ConfirmedReservation.class));
        }

        ReservationHandler reservationHandler = new ReservationHandler(attractionSpy,
                VALID_DAY_OF_YEAR, Mockito.mock(ReservationObserver.class),
                SLOT_CAPACITY, confirmedReservations, new LinkedHashMap[TOTAL_SLOTS]);
        ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
        attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

        bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlotFrom(TIME_FROM_STRING)
                        .setSlotTo(ParseUtils.formatTime(TIME_TO_LOCAL_TIME.minusMinutes(SLOT_DURATION_MINUTES)))
                        .build(),
                availabilityResponseObserver);

        ArgumentCaptor<AvailabilityResponse> responseCaptor = ArgumentCaptor.forClass(AvailabilityResponse.class);
        Mockito.verify(availabilityResponseObserver).onNext(responseCaptor.capture());
        AvailabilityResponse capturedResponse = responseCaptor.getValue();
        assertEquals(TOTAL_SLOTS, capturedResponse.getSlotList().size());
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            assertEquals(SLOT_CAPACITY, capturedResponse.getSlot(i).getSlotCapacity());
            assertEquals(ATTRACTION_NAME, capturedResponse.getSlot(i).getAttractionName());
            assertEquals(1, capturedResponse.getSlot(i).getBookingsConfirmed());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsPending());
            assertEquals(TIME_FROM_LOCAL_TIME.plusMinutes((long) i * SLOT_DURATION_MINUTES).toString(), capturedResponse.getSlot(i).getSlot());
        }
    }

    @Test
    public void testCheckAvailabilityPending() {
        Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction attractionSpy = Mockito.spy(attraction);
        attractions.put(ATTRACTION_NAME, attractionSpy);

        LinkedHashMap<UUID, Reservation>[] pendingReservations = (LinkedHashMap<UUID, Reservation>[]) new LinkedHashMap[TOTAL_SLOTS];

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            pendingReservations[i] = new LinkedHashMap<>();
            pendingReservations[i].put(VISITOR_ID, Mockito.mock(Reservation.class));
        }

        ReservationHandler reservationHandler = new ReservationHandler(attractionSpy,
                VALID_DAY_OF_YEAR, Mockito.mock(ReservationObserver.class),
                SLOT_CAPACITY, new Map[TOTAL_SLOTS], pendingReservations);
        ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
        attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

        bookingService.checkAttractionAvailability(AvailabilityRequest.newBuilder()
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlotFrom(TIME_FROM_STRING)
                        .setSlotTo(ParseUtils.formatTime(TIME_TO_LOCAL_TIME.minusMinutes(SLOT_DURATION_MINUTES)))
                        .build(),
                availabilityResponseObserver);

        ArgumentCaptor<AvailabilityResponse> responseCaptor = ArgumentCaptor.forClass(AvailabilityResponse.class);
        Mockito.verify(availabilityResponseObserver).onNext(responseCaptor.capture());
        AvailabilityResponse capturedResponse = responseCaptor.getValue();
        assertEquals(TOTAL_SLOTS, capturedResponse.getSlotList().size());
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            assertEquals(SLOT_CAPACITY, capturedResponse.getSlot(i).getSlotCapacity());
            assertEquals(ATTRACTION_NAME, capturedResponse.getSlot(i).getAttractionName());
            assertEquals(0, capturedResponse.getSlot(i).getBookingsConfirmed());
            assertEquals(1, capturedResponse.getSlot(i).getBookingsPending());
            assertEquals(TIME_FROM_LOCAL_TIME.plusMinutes((long) i * SLOT_DURATION_MINUTES).toString(), capturedResponse.getSlot(i).getSlot());
        }
    }

    @Test
    public void testConfirmReservationFailureLessThan0Days() {
        assertThrows(InvalidDayException.class, () -> {
            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(INVALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureMoreThan365Days() {
        assertThrows(InvalidDayException.class, () -> {
            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(OTHER_INVALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureEmptyAttractionName() {
        assertThrows(EmptyAttractionException.class, () -> {
            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName("")
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureBlankAttractionName() {
        assertThrows(EmptyAttractionException.class, () -> {
            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName("  ")
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureNoAttraction() {
        assertThrows(AttractionNotFoundException.class, () -> {

            Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_FULL_DAY);
            Ticket ticketSpy = Mockito.spy(ticket);
            ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(NON_EXISTING_ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureNoCapacityDefined() {
        assertThrows(CapacityNotDefinedException.class, () -> {
            Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
            Attraction attractionSpy = Mockito.spy(attraction);
            attractions.put(ATTRACTION_NAME, attractionSpy);

            Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_FULL_DAY);
            Ticket ticketSpy = Mockito.spy(ticket);
            ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);


            ReservationHandler reservationHandler = attraction.getReservationHandler(VALID_DAY_OF_YEAR);
            ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
            attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureReservationAlreadyConfirmed() {
        assertThrows(ReservationAlreadyConfirmedException.class, () -> {
            Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
            Attraction attractionSpy = Mockito.spy(attraction);
            attractions.put(ATTRACTION_NAME, attractionSpy);

            Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_FULL_DAY);
            Ticket ticketSpy = Mockito.spy(ticket);
            ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);


            Map<UUID, ar.edu.itba.pod.grpc.server.models.ConfirmedReservation>[] confirmedReservations = new Map[TOTAL_SLOTS];

            ar.edu.itba.pod.grpc.server.models.ConfirmedReservation reservation = Mockito.mock(ar.edu.itba.pod.grpc.server.models.ConfirmedReservation.class);
            confirmedReservations[0] = new ConcurrentHashMap<>();
            confirmedReservations[0].put(VISITOR_ID, reservation);

            ReservationHandler reservationHandler = new ReservationHandler(attractionSpy,
                    VALID_DAY_OF_YEAR, Mockito.mock(ReservationObserver.class),
                    SLOT_CAPACITY, confirmedReservations, new LinkedHashMap[TOTAL_SLOTS]);

            ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
            attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureReservationNotFound() {
        assertThrows(ReservationNotFoundException.class, () -> {
            Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
            Attraction attractionSpy = Mockito.spy(attraction);
            attractions.put(ATTRACTION_NAME, attractionSpy);

            Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_FULL_DAY);
            Ticket ticketSpy = Mockito.spy(ticket);
            ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);

            Map<UUID, ar.edu.itba.pod.grpc.server.models.ConfirmedReservation>[] confirmedReservations = new Map[TOTAL_SLOTS];

            confirmedReservations[0] = new ConcurrentHashMap<>();

            ReservationHandler reservationHandler = new ReservationHandler(attractionSpy,
                    VALID_DAY_OF_YEAR, Mockito.mock(ReservationObserver.class),
                    SLOT_CAPACITY, confirmedReservations, new LinkedHashMap[TOTAL_SLOTS]);

            ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
            attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureInvalidSlot() {
        assertThrows(InvalidSlotException.class, () -> {
            Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
            Attraction attractionSpy = Mockito.spy(attraction);
            attractions.put(ATTRACTION_NAME, attractionSpy);

            Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_FULL_DAY);
            Ticket ticketSpy = Mockito.spy(ticket);
            ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);


            Map<UUID, ConfirmedReservation>[] confirmedReservations = new Map[TOTAL_SLOTS];

            confirmedReservations[0] = new ConcurrentHashMap<>();

            ReservationHandler reservationHandler = new ReservationHandler(attractionSpy,
                    VALID_DAY_OF_YEAR, Mockito.mock(ReservationObserver.class),
                    SLOT_CAPACITY, confirmedReservations, new LinkedHashMap[TOTAL_SLOTS]);

            ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
            attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_TO_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservationFailureNoTicket() {
        assertThrows(MissingPassException.class, () -> {
            Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
            Attraction attractionSpy = Mockito.spy(attraction);
            attractions.put(ATTRACTION_NAME, attractionSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));

        });
    }

    @Test
    public void testConfirmReservationFailureCantBookWithHalfDayPass() {
        assertThrows(MissingPassException.class, () -> {
            Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
            Attraction attractionSpy = Mockito.spy(attraction);
            attractions.put(ATTRACTION_NAME, attractionSpy);

            Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_HALF_DAY);
            Ticket ticketSpy = Mockito.spy(ticket);
            ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);

            bookingService.confirmReservation(BookingRequest.newBuilder()
                            .setAttractionName(ATTRACTION_NAME)
                            .setDayOfYear(VALID_DAY_OF_YEAR)
                            .setSlot("14:00")
                            .setVisitorId(VISITOR_ID.toString())
                            .build(),
                    Mockito.mock(StreamObserver.class));
        });
    }

    @Test
    public void testConfirmReservation() {
        Attraction attraction = new Attraction(ATTRACTION_NAME, TIME_FROM_LOCAL_TIME, TIME_TO_LOCAL_TIME, SLOT_DURATION_MINUTES);
        Attraction attractionSpy = Mockito.spy(attraction);
        attractions.put(ATTRACTION_NAME, attractionSpy);

        Ticket ticket = new Ticket(VISITOR_ID, VALID_DAY_OF_YEAR, TICKET_TYPE_FULL_DAY);
        Ticket ticketSpy = Mockito.spy(ticket);
        ticketsByDay[VALID_DAY_OF_YEAR - 1].put(VISITOR_ID, ticketSpy);


        LinkedHashMap<UUID, Reservation>[] pendingReservations = (LinkedHashMap<UUID, Reservation>[]) new LinkedHashMap[TOTAL_SLOTS];
        pendingReservations[0] = new LinkedHashMap<>();

        Reservation reservation = new Reservation(Mockito.mock(Ticket.class), Mockito.mock(Attraction.class));
        Reservation reservationSpy = Mockito.spy(reservation);
        pendingReservations[0].put(VISITOR_ID, reservationSpy);

        ReservationHandler reservationHandler = new ReservationHandler(attractionSpy,
                VALID_DAY_OF_YEAR, Mockito.mock(ReservationObserver.class),
                SLOT_CAPACITY, new Map[TOTAL_SLOTS], pendingReservations);

        ReservationHandler reservationHandlerSpy = Mockito.spy(reservationHandler);
        attractionSpy.setReservationHandler(VALID_DAY_OF_YEAR, reservationHandlerSpy);

        bookingService.confirmReservation(BookingRequest.newBuilder()
                        .setAttractionName(ATTRACTION_NAME)
                        .setDayOfYear(VALID_DAY_OF_YEAR)
                        .setSlot(ParseUtils.formatTime(TIME_FROM_LOCAL_TIME))
                        .setVisitorId(VISITOR_ID.toString())
                        .build(),
                Mockito.mock(StreamObserver.class));
    }
}
