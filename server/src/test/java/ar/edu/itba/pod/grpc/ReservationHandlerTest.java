package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.ReservationHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ReservationHandlerTest {
    private static final String ATTRACTION_NAME1 = "Fuerza Electromagnética";
    private static final LocalTime ATTRACTION_OPENING_TIME1 = LocalTime.of(10, 0);
    private static final LocalTime ATTRACTION_CLOSING_TIME1 = LocalTime.of(16, 0);
    private static final int ATTRACTION_SLOT_DURATION1 = 50;
    private static final int DAY_OF_YEAR1 = 69;
    private static final LocalTime[] VALID_TIME_SLOTS1 = new LocalTime[] {
            LocalTime.of(10, 0), LocalTime.of(10, 50), LocalTime.of(11, 40),
            LocalTime.of(12, 30), LocalTime.of(13, 20), LocalTime.of(14, 10),
            LocalTime.of(15, 0), LocalTime.of(15, 50)
    };

    private static final String ATTRACTION_NAME2 = "Fuerza Gravitatoria";
    private static final LocalTime ATTRACTION_OPENING_TIME2 = LocalTime.of(4, 0);
    private static final LocalTime ATTRACTION_CLOSING_TIME2 = LocalTime.of(10, 0);
    private static final int ATTRACTION_SLOT_DURATION2 = 30;
    private static final int DAY_OF_YEAR2 = 69;
    private static final LocalTime[] VALID_TIME_SLOTS2 = new LocalTime[] {
            LocalTime.of(4, 0), LocalTime.of(4, 30), LocalTime.of(5, 0),
            LocalTime.of(5, 30), LocalTime.of(6, 0), LocalTime.of(6, 30),
            LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0),
            LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30)
    };


    private final static UUID user1 = UUID.fromString("00000000-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID user2 = UUID.fromString("11111111-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID user3 = UUID.fromString("22222222-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID user4 = UUID.fromString("33333333-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID user5 = UUID.fromString("44444444-7dec-11d0-a765-00a0c91e6bf6");
    private final static UUID user6 = UUID.fromString("55555555-7dec-11d0-a765-00a0c91e6bf6");


    @Mock
    private Attraction attraction1;

    @Mock
    private Attraction attraction2;

    private ReservationHandler reservationHandler1;
    private ReservationHandler reservationHandler2;

    @Before
    public void setUp() {
        // when(attraction1.getName()).thenReturn(ATTRACTION_NAME1);
        when(attraction1.getOpeningTime()).thenReturn(ATTRACTION_OPENING_TIME1);
        when(attraction1.getClosingTime()).thenReturn(ATTRACTION_CLOSING_TIME1);
        when(attraction1.getSlotDuration()).thenReturn(ATTRACTION_SLOT_DURATION1);
        reservationHandler1 = new ReservationHandler(attraction1, DAY_OF_YEAR1);

        // when(attraction2.getName()).thenReturn(ATTRACTION_NAME2);
        when(attraction2.getOpeningTime()).thenReturn(ATTRACTION_OPENING_TIME2);
        when(attraction2.getClosingTime()).thenReturn(ATTRACTION_CLOSING_TIME2);
        when(attraction2.getSlotDuration()).thenReturn(ATTRACTION_SLOT_DURATION2);
        reservationHandler2 = new ReservationHandler(attraction2, DAY_OF_YEAR2);
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
}
