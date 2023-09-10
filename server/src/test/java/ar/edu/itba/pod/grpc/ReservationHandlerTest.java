package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.server.models.*;
import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.results.MakeReservationResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ReservationHandlerTest {
    private static final String ATTRACTION_NAME1 = "Fuerza Electromagnética";
    private static final LocalTime ATTRACTION_OPENING_TIME1 = LocalTime.of(10, 0);
    private static final LocalTime ATTRACTION_CLOSING_TIME1 = LocalTime.of(16, 0);
    private static final LocalTime INVALID_TIME1 = LocalTime.of(16, 0);
    private static final int ATTRACTION_SLOT_DURATION1 = 50;
    private static final int DAY_OF_YEAR = 69;
    private static final LocalTime[] VALID_TIME_SLOTS1 = new LocalTime[] {
            LocalTime.of(10, 0), LocalTime.of(10, 50), LocalTime.of(11, 40),
            LocalTime.of(12, 30), LocalTime.of(13, 20), LocalTime.of(14, 10),
            LocalTime.of(15, 0), LocalTime.of(15, 50)
    };

    private static final String ATTRACTION_NAME2 = "Fuerza Gravitatoria";
    private static final LocalTime ATTRACTION_OPENING_TIME2 = LocalTime.of(4, 0);
    private static final LocalTime ATTRACTION_CLOSING_TIME2 = LocalTime.of(10, 0);
    private static final LocalTime INVALID_TIME2 = LocalTime.of(10, 0);
    private static final int ATTRACTION_SLOT_DURATION2 = 30;
    private static final int ATTRACTION_SLOT_CAPACITY2 = 15;
    private static final LocalTime[] VALID_TIME_SLOTS2 = new LocalTime[] {
            LocalTime.of(4, 0), LocalTime.of(4, 30), LocalTime.of(5, 0),
            LocalTime.of(5, 30), LocalTime.of(6, 0), LocalTime.of(6, 30),
            LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0),
            LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30)
    };


    private final static UUID USER1 = UUID.fromString("00000000-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID USER2 = UUID.fromString("11111111-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID USER3 = UUID.fromString("22222222-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID USER4 = UUID.fromString("33333333-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID USER5 = UUID.fromString("44444444-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID USER6 = UUID.fromString("55555555-7dec-11d0-a765-00a0c91e6bf6");

    private final static Ticket TICKET1 = new Ticket(USER1, DAY_OF_YEAR, TicketType.HALF_DAY);

    private final static Ticket TICKET2 = new Ticket(USER2, DAY_OF_YEAR, TicketType.FULL_DAY);


    @Mock
    private Attraction attraction1;

    @Mock
    private Attraction attraction2;

    private Map<UUID, Reservation>[] slotConfirmedRequests1 = (Map<UUID, Reservation>[]) new Map[VALID_TIME_SLOTS1.length];
    private LinkedHashMap<UUID, Reservation>[] slotPendingRequests1 = (LinkedHashMap<UUID, Reservation>[]) new LinkedHashMap[VALID_TIME_SLOTS1.length];
    private ReservationHandler reservationHandler1;

    private Map<UUID, Reservation>[] slotConfirmedRequests2 = (Map<UUID, Reservation>[]) new Map[VALID_TIME_SLOTS2.length];
    private LinkedHashMap<UUID, Reservation>[] slotPendingRequests2 = (LinkedHashMap<UUID, Reservation>[]) new LinkedHashMap[VALID_TIME_SLOTS2.length];
    private ReservationHandler reservationHandler2;

    @Before
    public void setUp() {
        // when(attraction1.getName()).thenReturn(ATTRACTION_NAME1);
        when(attraction1.getOpeningTime()).thenReturn(ATTRACTION_OPENING_TIME1);
        when(attraction1.getClosingTime()).thenReturn(ATTRACTION_CLOSING_TIME1);
        when(attraction1.getSlotDuration()).thenReturn(ATTRACTION_SLOT_DURATION1);
        reservationHandler1 = new ReservationHandler(attraction1, DAY_OF_YEAR, -1, slotConfirmedRequests1, slotPendingRequests1);

        // when(attraction2.getName()).thenReturn(ATTRACTION_NAME2);
        when(attraction2.getOpeningTime()).thenReturn(ATTRACTION_OPENING_TIME2);
        when(attraction2.getClosingTime()).thenReturn(ATTRACTION_CLOSING_TIME2);
        when(attraction2.getSlotDuration()).thenReturn(ATTRACTION_SLOT_DURATION2);
        reservationHandler2 = new ReservationHandler(attraction2, DAY_OF_YEAR, ATTRACTION_SLOT_CAPACITY2, slotConfirmedRequests2, slotPendingRequests2);
    }

    @Test
    public void testSlotCountIncludesLastSlot() {
        assertEquals(VALID_TIME_SLOTS1.length, reservationHandler1.getSlotCount());
    }

    @Test
    public void testValidTimeSlotsAreValid() {
        for (LocalTime validTimeSlot : VALID_TIME_SLOTS1)
            assertTrue(reservationHandler1.isSlotTimeValid(validTimeSlot));
    }

    @Test
    public void testInvalidTimeSlotsAreInvalid() {
        int openingMinuteOfDay = ATTRACTION_OPENING_TIME1.toSecondOfDay() / 60;
        int closingMinuteOfDay = ATTRACTION_CLOSING_TIME1.toSecondOfDay() / 60;
        for (int i = openingMinuteOfDay; i < closingMinuteOfDay; i++)
            if ((i - openingMinuteOfDay) % ATTRACTION_SLOT_DURATION1 != 0)
                assertFalse(reservationHandler1.isSlotTimeValid(LocalTime.ofSecondOfDay(i * 60L)));
    }

    @Test
    public void testTimeSlotsBeforeOpeningAreInvalid() {
        int openingMinuteOfDay = ATTRACTION_OPENING_TIME1.toSecondOfDay() / 60;
        for (int i = 0; i < openingMinuteOfDay; i++)
            assertFalse(reservationHandler1.isSlotTimeValid(LocalTime.ofSecondOfDay(i * 60L)));
    }

    @Test
    public void testTimeSlotsAfterClosingAreInvalid() {
        int closingMinuteOfDay = ATTRACTION_CLOSING_TIME1.toSecondOfDay() / 60;
        int minutesInDay = 24 * 60;
        for (int i = closingMinuteOfDay; i < minutesInDay; i++)
            assertFalse(reservationHandler1.isSlotTimeValid(LocalTime.ofSecondOfDay(i * 60L)));
    }

    @Test
    public void testClosingTimeSlotIsInvalidExact() {
        assertFalse(reservationHandler2.isSlotTimeValid(ATTRACTION_CLOSING_TIME2));
    }

    @Test
    public void testMakeReservationWithInvalidTime() {
        MakeReservationResult result = reservationHandler1.makeReservation(TICKET1, INVALID_TIME1);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testMakeReservationSlotCapacityUndefined() {
        MakeReservationResult result = reservationHandler1.makeReservation(TICKET1, VALID_TIME_SLOTS1[3]);

        assertTrue(result.isSuccess());
        assertFalse(result.reservation().isConfirmed());
        assertEquals(MakeReservationResult.Status.QUEUED, result.status());
        assertEquals(attraction1, result.reservation().getAttraction());
        assertEquals(DAY_OF_YEAR, result.reservation().getDayOfYear());
        assertEquals(TICKET1, result.reservation().getTicket());
        assertEquals(VALID_TIME_SLOTS1[3], result.reservation().getSlotTime());
        assertTrue(slotConfirmedRequests1[3] == null || slotConfirmedRequests1[3].isEmpty());
        assertEquals(slotPendingRequests1[3].get(TICKET1.getVisitorId()), result.reservation());
    }

    @Test
    public void testMakeReservationSlotCapacityUndefinedExisting() {
        slotPendingRequests1[3] = new LinkedHashMap<>();
        Reservation existingReservation = new Reservation(TICKET1, attraction1, VALID_TIME_SLOTS1[3], false);
        slotPendingRequests1[3].put(TICKET1.getVisitorId(), existingReservation);

        MakeReservationResult result = reservationHandler1.makeReservation(TICKET1, VALID_TIME_SLOTS1[3]);

        assertFalse(result.isSuccess());
        assertNull(result.reservation());
        assertEquals(MakeReservationResult.Status.ALREADY_EXISTS, result.status());
        assertTrue(slotConfirmedRequests1[3] == null || slotConfirmedRequests1[3].isEmpty());
        assertSame(existingReservation, slotPendingRequests1[3].get(TICKET1.getVisitorId()));
    }

    @Test
    public void testMakeReservationSlotCapacityDefined() {
        MakeReservationResult result = reservationHandler2.makeReservation(TICKET2, VALID_TIME_SLOTS2[3]);

        assertTrue(result.isSuccess());
        assertTrue(result.reservation().isConfirmed());
        assertEquals(MakeReservationResult.Status.CONFIRMED, result.status());
        assertEquals(attraction2, result.reservation().getAttraction());
        assertEquals(DAY_OF_YEAR, result.reservation().getDayOfYear());
        assertEquals(TICKET2, result.reservation().getTicket());
        assertEquals(VALID_TIME_SLOTS2[3], result.reservation().getSlotTime());
        assertTrue(slotPendingRequests2[3] == null || slotPendingRequests2[3].isEmpty());
        assertEquals(slotConfirmedRequests2[3].get(TICKET2.getVisitorId()), result.reservation());
    }

    @Test
    public void testMakeReservationSlotCapacityDefinedExistingPending() {
        slotPendingRequests2[3] = new LinkedHashMap<>();
        Reservation existingReservation = new Reservation(TICKET2, attraction2, VALID_TIME_SLOTS2[3], false);
        slotPendingRequests2[3].put(TICKET2.getVisitorId(), existingReservation);

        MakeReservationResult result = reservationHandler2.makeReservation(TICKET2, VALID_TIME_SLOTS2[3]);

        assertFalse(result.isSuccess());
        assertNull(result.reservation());
        assertEquals(MakeReservationResult.Status.ALREADY_EXISTS, result.status());
        assertTrue(slotConfirmedRequests2[3] == null || slotConfirmedRequests2[3].isEmpty());
        assertSame(existingReservation, slotPendingRequests2[3].get(TICKET2.getVisitorId()));
    }

    @Test
    public void testMakeReservationSlotCapacityDefinedExistingConfirmed() {
        slotConfirmedRequests2[3] = new HashMap<>();
        Reservation existingReservation = new Reservation(TICKET2, attraction2, VALID_TIME_SLOTS2[3], true);
        slotConfirmedRequests2[3].put(TICKET2.getVisitorId(), existingReservation);

        MakeReservationResult result = reservationHandler2.makeReservation(TICKET2, VALID_TIME_SLOTS2[3]);

        assertFalse(result.isSuccess());
        assertNull(result.reservation());
        assertEquals(MakeReservationResult.Status.ALREADY_EXISTS, result.status());
        assertTrue(slotPendingRequests2[3] == null || slotPendingRequests2[3].isEmpty());
        assertSame(existingReservation, slotConfirmedRequests2[3].get(TICKET2.getVisitorId()));
    }

    @Test(expected = IllegalStateException.class)
    public void testConfirmReservationBeforeSlotCapacityDefined() {
        reservationHandler2.confirmReservation(TICKET1.getVisitorId(), VALID_TIME_SLOTS1[3]);
    }

    @Test
    public void testConfirmReservationWhenPending() {
        slotPendingRequests2[3] = new LinkedHashMap<>();
        Reservation existingReservation = new Reservation(TICKET2, attraction2, VALID_TIME_SLOTS2[3], false);
        slotPendingRequests2[3].put(TICKET2.getVisitorId(), existingReservation);

        reservationHandler2.confirmReservation(TICKET2.getVisitorId(), VALID_TIME_SLOTS2[3]);

        assertTrue(existingReservation.isConfirmed());
        assertTrue(slotPendingRequests2[3] == null || slotPendingRequests2[3].isEmpty());
        assertSame(existingReservation, slotConfirmedRequests2[3].get(TICKET2.getVisitorId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfirmReservationWhenNonExisting() {
        reservationHandler1.confirmReservation(TICKET1.getVisitorId(), VALID_TIME_SLOTS1[3]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfirmReservationWhenAlreadyConfirmed() {
        slotConfirmedRequests2[3] = new HashMap<>();
        Reservation existingReservation = new Reservation(TICKET2, attraction2, VALID_TIME_SLOTS2[3], true);
        slotConfirmedRequests2[3].put(TICKET2.getVisitorId(), existingReservation);

        reservationHandler2.confirmReservation(TICKET2.getVisitorId(), VALID_TIME_SLOTS2[3]);
    }

    @Test
    public void testCancelReservationWhenPending() {
        slotPendingRequests2[3] = new LinkedHashMap<>();
        Reservation existingReservation = new Reservation(TICKET2, attraction2, VALID_TIME_SLOTS2[3], false);
        slotPendingRequests2[3].put(TICKET2.getVisitorId(), existingReservation);

        reservationHandler2.cancelReservation(TICKET2.getVisitorId(), VALID_TIME_SLOTS2[3]);

        assertTrue(slotPendingRequests2[3] == null || slotPendingRequests2[3].isEmpty());
        assertTrue(slotConfirmedRequests2[3] == null || slotConfirmedRequests2[3].isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelReservationWhenNonExisting() {
        reservationHandler1.cancelReservation(TICKET1.getVisitorId(), VALID_TIME_SLOTS1[3]);
    }

    @Test
    public void testCancelReservationWhenConfirmed() {
        slotConfirmedRequests1[3] = new HashMap<>();
        Reservation existingReservation = new Reservation(TICKET1, attraction1, VALID_TIME_SLOTS1[3], true);
        slotConfirmedRequests1[3].put(TICKET1.getVisitorId(), existingReservation);

        reservationHandler1.cancelReservation(TICKET1.getVisitorId(), VALID_TIME_SLOTS1[3]);

        assertTrue(slotPendingRequests1[3] == null || slotPendingRequests1[3].isEmpty());
        assertTrue(slotConfirmedRequests1[3] == null || slotConfirmedRequests1[3].isEmpty());
    }
}
