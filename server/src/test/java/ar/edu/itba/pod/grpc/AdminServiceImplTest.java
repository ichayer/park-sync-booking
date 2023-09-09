package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.server.models.Attraction;
import ar.edu.itba.pod.grpc.server.models.AttractionHandler;
import ar.edu.itba.pod.grpc.server.models.Ticket;
import ar.edu.itba.pod.grpc.server.models.TicketType;
import ar.edu.itba.pod.grpc.server.services.AdminServiceImpl;
import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final int INVALID_SLOT_GAP = 61;
    private static final int DAYS_IN_YEAR = 365;
    private static final PassType FULL_DAY_PASS_TYPE = PassType.PASS_TYPE_FULL_DAY;
    private static final PassType HALF_DAY_PASS_TYPE = PassType.PASS_TYPE_HALF_DAY;
    private static final String DEFAULT_VISITOR_ID_STRING = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
    private static final UUID DEFAULT_VISITOR_ID_UUID = UUID.fromString(DEFAULT_VISITOR_ID_STRING);
    private static final int VALID_DATE = 1;
    private static final int OTHER_VALID_DATE = 365;
    private static final int INVALID_DATE = 366;
    private static final int OTHER_INVALID_DATE = 0;
    private static final int VALID_CAPACITY = 10;
    private static final int INVALID_CAPACITY = -1;
    private final Map<String, Attraction> attractions = new ConcurrentHashMap<>();
    private final Map<UUID, Map<Integer, Ticket>> tickets = new ConcurrentHashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final AttractionHandler attractionHandler = new AttractionHandler(attractions, tickets);
    private final AdminServiceImpl adminService = new AdminServiceImpl(attractionHandler);
    @Mock
    private StreamObserver<BoolValue> booleanResponseObserver;
    @Mock
    private StreamObserver<AddCapacityResponse> capacityResponseObserver;

    @BeforeEach
    public void setUp() {
        attractions.clear();
        tickets.clear();
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
        attractions.put(ANOTHER_ATTRACTION_NAME, attraction);

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

        attractions.put(ATTRACTION_NAME, attraction);

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
                .setDayOfYear(VALID_DATE)
                .setPassType(FULL_DAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertNotNull(tickets.get(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID_UUID).size());
    }

    @Test
    public void testAddTicketFailureMoreThan365Days() {
        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(INVALID_DATE)
                .setPassType(FULL_DAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertFalse(tickets.containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(0, tickets.size());
    }

    @Test
    public void testAddTicketFailureLessThan1Day() {
        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(OTHER_INVALID_DATE)
                .setPassType(FULL_DAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertFalse(tickets.containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(0, tickets.size());
    }

    @Test
    public void testAddSameTicketPassForSameDate() {
        Ticket ticket = new Ticket(DEFAULT_VISITOR_ID_UUID, VALID_DATE, TicketType.FULL_DAY);
        tickets.put(DEFAULT_VISITOR_ID_UUID, new HashMap<>());
        tickets.get(DEFAULT_VISITOR_ID_UUID).put(VALID_DATE, ticket);

        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(VALID_DATE)
                .setPassType(FULL_DAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID_UUID).size());
        Assert.assertEquals(tickets.get(DEFAULT_VISITOR_ID_UUID).get(VALID_DATE), ticket);
    }

    @Test
    public void testAddOtherPassForSameDate() {
        Ticket ticket = new Ticket(DEFAULT_VISITOR_ID_UUID, VALID_DATE, TicketType.FULL_DAY);
        tickets.put(DEFAULT_VISITOR_ID_UUID, new HashMap<>());
        tickets.get(DEFAULT_VISITOR_ID_UUID).put(VALID_DATE, ticket);

        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(VALID_DATE)
                .setPassType(HALF_DAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID_UUID).size());
        Assert.assertEquals(tickets.get(DEFAULT_VISITOR_ID_UUID).get(VALID_DATE), ticket);
    }

    @Test
    public void testAddTicketForOtherDay() {
        Ticket ticket = new Ticket(DEFAULT_VISITOR_ID_UUID, VALID_DATE, TicketType.FULL_DAY);
        tickets.put(DEFAULT_VISITOR_ID_UUID, new HashMap<>());
        tickets.get(DEFAULT_VISITOR_ID_UUID).put(VALID_DATE, ticket);

        AddTicketRequest request = AddTicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID_STRING)
                .setDayOfYear(OTHER_VALID_DATE)
                .setPassType(FULL_DAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, booleanResponseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID_UUID));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(2, tickets.get(DEFAULT_VISITOR_ID_UUID).size());
        Assert.assertEquals(tickets.get(DEFAULT_VISITOR_ID_UUID).get(VALID_DATE), ticket);
        Assert.assertEquals(tickets.get(DEFAULT_VISITOR_ID_UUID).get(OTHER_VALID_DATE).getVisitorId(), DEFAULT_VISITOR_ID_UUID);
        Assert.assertEquals(tickets.get(DEFAULT_VISITOR_ID_UUID).get(OTHER_VALID_DATE).getDayOfYear(), OTHER_VALID_DATE);
        Assert.assertEquals(tickets.get(DEFAULT_VISITOR_ID_UUID).get(OTHER_VALID_DATE).getTicketType(), TicketType.FULL_DAY);
    }

}
