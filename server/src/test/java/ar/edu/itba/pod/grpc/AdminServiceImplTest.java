package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.models.Ticket;
import ar.edu.itba.pod.grpc.server.models.TicketType;
import ar.edu.itba.pod.grpc.server.services.AdminServiceImpl;
import ar.edu.itba.pod.grpc.server.utils.Constants;
import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplTest {
    private static final String ATTRACTION_NAME = "attractionName";
    private static final String ANOTHER_ATTRACTION_NAME = "anotherAttractionName";
    private static final String INVALID_ATTRACTION_NAME = "";
    private static final String OPENING_TIME = "10:00";
    private static final String CLOSING_TIME = "18:00";
    private static final String INVALID_HOURS_FROM = "25:00";
    private static final String INVALID_HOURS_TO = "18:61";
    private static final String INVALID_HOURS_FROM_FORMAT = "10:0";
    private static final String INVALID_HOURS_TO_FORMAT = "10:00:00";
    private static final int SLOT_GAP = 10;
    private static final int NO_SLOT_GAP = 0;
    private static final int NEGATIVE_SLOT_GAP = -1;
    private static final String DEFAULT_VISITOR_ID_STRING = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
    private static final UUID DEFAULT_VISITOR_ID_UUID = UUID.fromString(DEFAULT_VISITOR_ID_STRING);
    private static final int VALID_DAY_OF_YEAR = 1;
    private static final int OTHER_VALID_DAY_OF_YEAR = 365;
    private static final int INVALID_DAY_OF_YEAR = 366;
    private static final int OTHER_INVALID_DAY_OF_YEAR = 0;
    private static final int VALID_CAPACITY = 10;
    private static final int INVALID_CAPACITY = -1;

    private final ConcurrentMap<String, Attraction> attractions = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Ticket> ticketsByDay[] = TestUtils.generateTicketsByDayMaps();
    private final AttractionHandler attractionHandler = new AttractionHandler(attractions, ticketsByDay);
    private final AdminServiceImpl adminService = new AdminServiceImpl(attractionHandler);

    @Mock
    private StreamObserver<BoolValue> booleanResponseObserver;
    @Mock
    private StreamObserver<AddCapacityResponse> capacityResponseObserver;

    @BeforeEach
    public void setUp() {
        attractions.clear();
        for (int i = 0; i < ticketsByDay.length; i++)
            ticketsByDay[i].clear();
    }

    @Test
    public void testAddAttraction() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setClosingTime(CLOSING_TIME)
                .setOpeningTime(OPENING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);
        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
    }

    @Test
    public void testAddAnotherAttraction() {
        Attraction attraction = new Attraction(ANOTHER_ATTRACTION_NAME, LocalTime.parse(OPENING_TIME), LocalTime.parse(CLOSING_TIME), SLOT_GAP);
        attractions.put(attraction.getName(), attraction);

        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setClosingTime(CLOSING_TIME)
                .setOpeningTime(OPENING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
        Assert.assertTrue(attractions.containsKey(ANOTHER_ATTRACTION_NAME));
        Assert.assertEquals(2, attractions.size());
    }

    @Test
    public void testAddAttractionWithExistingName() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(OPENING_TIME)
                .setClosingTime(CLOSING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        final Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(OPENING_TIME), LocalTime.parse(CLOSING_TIME), SLOT_GAP);

        attractions.put(attraction.getName(), attraction);

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
        Assert.assertEquals(1, attractions.size());
    }

    @Test
    public void testAddAttractionWithInvalidName() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(INVALID_ATTRACTION_NAME)
                .setOpeningTime(OPENING_TIME)
                .setClosingTime(CLOSING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidSlotGapNoMinutes() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(OPENING_TIME)
                .setClosingTime(CLOSING_TIME)
                .setSlotDurationMinutes(NO_SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidSlotGapNegativeMinutes() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(OPENING_TIME)
                .setClosingTime(CLOSING_TIME)
                .setSlotDurationMinutes(NEGATIVE_SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHours() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(CLOSING_TIME)
                .setClosingTime(OPENING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourForm() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(INVALID_HOURS_FROM)
                .setClosingTime(CLOSING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourTo() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(OPENING_TIME)
                .setClosingTime(INVALID_HOURS_TO)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourFormFormat() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(INVALID_HOURS_FROM_FORMAT)
                .setClosingTime(CLOSING_TIME)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourToFormat() {
        final AddAttractionRequest request = AddAttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setOpeningTime(OPENING_TIME)
                .setClosingTime(INVALID_HOURS_TO_FORMAT)
                .setSlotDurationMinutes(SLOT_GAP)
                .build();

        adminService.addAttraction(request, booleanResponseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddTicket() {
        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(VALID_DAY_OF_YEAR)
                .setPassType(PassType.PASS_TYPE_FULL_DAY)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(ticketsByDay[VALID_DAY_OF_YEAR - 1].containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertNotNull(ticketsByDay[VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, ticketsByDay[VALID_DAY_OF_YEAR - 1].size());
    }

    @Test
    public void testAddTicketFailureMoreThan365Days() {
        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(INVALID_DAY_OF_YEAR)
                .setPassType(PassType.PASS_TYPE_FULL_DAY)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        for (int i = 0; i < ticketsByDay.length; i++)
            Assert.assertTrue(ticketsByDay[i].isEmpty());
    }

    @Test
    public void testAddTicketFailureLessThan1Day() {
        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(OTHER_INVALID_DAY_OF_YEAR)
                .setPassType(PassType.PASS_TYPE_FULL_DAY)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        for (int i = 0; i < ticketsByDay.length; i++)
            Assert.assertTrue(ticketsByDay[i].isEmpty());
    }

    @Test
    public void testAddSameTicketPassForSameDate() {
        Ticket ticket = new Ticket(DEFAULT_VISITOR_ID_UUID, VALID_DAY_OF_YEAR, TicketType.FULL_DAY);
        ticketsByDay[ticket.getDayOfYear() - 1].put(ticket.getVisitorId(), ticket);

        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(VALID_DAY_OF_YEAR)
                .setPassType(PassType.PASS_TYPE_FULL_DAY)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(ticketsByDay[VALID_DAY_OF_YEAR - 1].containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, ticketsByDay[VALID_DAY_OF_YEAR - 1].size());
        Assert.assertEquals(ticketsByDay[VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID), ticket);
    }

    @Test
    public void testAddOtherPassForSameDate() {
        Ticket ticket = new Ticket(DEFAULT_VISITOR_ID_UUID, VALID_DAY_OF_YEAR, TicketType.FULL_DAY);
        ticketsByDay[ticket.getDayOfYear() - 1].put(ticket.getVisitorId(), ticket);

        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(VALID_DAY_OF_YEAR)
                .setPassType(PassType.PASS_TYPE_HALF_DAY)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(ticketsByDay[VALID_DAY_OF_YEAR - 1].containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, ticketsByDay[VALID_DAY_OF_YEAR - 1].size());
        Assert.assertEquals(ticket, ticketsByDay[VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID));
    }

    @Test
    public void testAddTicketForOtherDay() {
        Ticket ticket = new Ticket(DEFAULT_VISITOR_ID_UUID, OTHER_VALID_DAY_OF_YEAR, TicketType.FULL_DAY);
        ticketsByDay[ticket.getDayOfYear() - 1].put(ticket.getVisitorId(), ticket);

        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(OTHER_VALID_DAY_OF_YEAR)
                .setPassType(PassType.PASS_TYPE_FULL_DAY)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(ticketsByDay[OTHER_VALID_DAY_OF_YEAR - 1].containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, ticketsByDay[OTHER_VALID_DAY_OF_YEAR - 1].size());
        Assert.assertEquals(ticket, ticketsByDay[OTHER_VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID));

        Assert.assertEquals(DEFAULT_VISITOR_ID_UUID, ticketsByDay[OTHER_VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID).getVisitorId());
        Assert.assertEquals(OTHER_VALID_DAY_OF_YEAR, ticketsByDay[OTHER_VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID).getDayOfYear());
        Assert.assertEquals(TicketType.FULL_DAY, ticketsByDay[OTHER_VALID_DAY_OF_YEAR - 1].get(DEFAULT_VISITOR_ID_UUID).getTicketType());
    }

}
