package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.server.services.AdminServiceImpl;
import io.grpc.stub.StreamObserver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplTest {

    private static final String ATTRACTION_NAME = "attractionName";
    private static final String HOURS_FROM = "10:00";
    private static final String HOURS_TO = "18:00";
    private static final String INVALID_HOURS_FROM = "25:00";
    private static final String INVALID_HOURS_TO = "18:61";
    private static final String INVALID_HOURS_FROM_FORMAT = "10:0";
    private static final String INVALID_HOURS_TO_FORMAT = "10:00:00";
    private static final int SLOT_GAP = 10;
    private static final int NO_SLOT_GAP = 0;
    private static final int NEGATIVE_SLOT_GAP = -1;
    private static final int INVALID_SLOT_GAP = 61;
    private static final PassType FULLDAY_PASS_TYPE = PassType.FULLDAY;
    private static final PassType HALFDAY_PASS_TYPE = PassType.HALFDAY;
    private static final String DEFAULT_VISITOR_ID = "1";
    private static final String VALID_DATE = "10-09-2023";
    private static final String OTHER_VALID_DATE = "11-10-2023";
    private static final String INVALID_DATE = "32-10-2023";
    private static final String INVALID_DATE_FORMAT = "/10/2023";
    private final Map<String, AttractionRequest> attractions = new HashMap<>();
    private final Map<String, Map<LocalDate, PassType>> tickets = new HashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @InjectMocks
    private final AdminServiceImpl adminService = new AdminServiceImpl(attractions, tickets);
    @Mock
    private StreamObserver<BooleanResponse> responseObserver;

    @BeforeEach
    public void setUp() {
        attractions.clear();
        tickets.clear();
    }

    @Test
    public void testAddAttraction() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);
        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
    }

    @Test
    public void testAddAttractionWithExistingName() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(SLOT_GAP)
                .build();

        attractions.put(ATTRACTION_NAME, request);

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
        Assert.assertEquals(1, attractions.size());
    }

    @Test
    public void testAddAttractionWithInvalidName() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName("")
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidSlotGapNoMinutes() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(NO_SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidSlotGapNegativeMinutes() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(NEGATIVE_SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidSlotGapMoreThan60Minutes() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(INVALID_SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHours() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_TO)
                .setHoursTo(HOURS_FROM)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourForm() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(INVALID_HOURS_FROM)
                .setHoursTo(HOURS_TO)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourTo() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(INVALID_HOURS_TO)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourFormFormat() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(INVALID_HOURS_FROM_FORMAT)
                .setHoursTo(HOURS_TO)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddAttractionWithInvalidHourToFormat() {
        final AttractionRequest request = AttractionRequest.newBuilder()
                .setName(ATTRACTION_NAME)
                .setHoursFrom(HOURS_FROM)
                .setHoursTo(INVALID_HOURS_TO_FORMAT)
                .setSlotGap(SLOT_GAP)
                .build();

        adminService.addAttraction(request, responseObserver);

        Assert.assertTrue(attractions.isEmpty());
    }

    @Test
    public void testAddTicket() {
        TicketRequest request = TicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID)
                .setDayOfYear(VALID_DATE)
                .setPassType(FULLDAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, responseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID).size());
    }

    @Test
    public void testAddTicketFailureInvalidDate() {
        TicketRequest request = TicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID)
                .setDayOfYear(INVALID_DATE)
                .setPassType(FULLDAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, responseObserver);

        Assert.assertFalse(tickets.containsKey(DEFAULT_VISITOR_ID));
        Assert.assertEquals(0, tickets.size());
    }

    @Test
    public void testAddTicketFailureInvalidDateFormat() {
        TicketRequest request = TicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID)
                .setDayOfYear(INVALID_DATE_FORMAT)
                .setPassType(FULLDAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, responseObserver);

        Assert.assertFalse(tickets.containsKey(DEFAULT_VISITOR_ID));
        Assert.assertEquals(0, tickets.size());
    }

    @Test
    public void testAddSameTicketPassForSameDate() {
        tickets.put(DEFAULT_VISITOR_ID, new HashMap<>());
        tickets.get(DEFAULT_VISITOR_ID).put(LocalDate.parse(VALID_DATE, formatter), FULLDAY_PASS_TYPE);
        TicketRequest request = TicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID)
                .setDayOfYear(VALID_DATE)
                .setPassType(FULLDAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, responseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID).size());
    }

    @Test
    public void testAddOtherPassForSameDate() {
        tickets.put(DEFAULT_VISITOR_ID, new HashMap<>());
        tickets.get(DEFAULT_VISITOR_ID).put(LocalDate.parse(VALID_DATE, formatter), FULLDAY_PASS_TYPE);
        TicketRequest request = TicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID)
                .setDayOfYear(VALID_DATE)
                .setPassType(HALFDAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, responseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID).size());
    }

    @Test
    public void testAddTicketForOtherDay() {
        tickets.put(DEFAULT_VISITOR_ID, new HashMap<>());
        tickets.get(DEFAULT_VISITOR_ID).put(LocalDate.parse(VALID_DATE, formatter), FULLDAY_PASS_TYPE);
        TicketRequest request = TicketRequest.newBuilder()
                .setVisitorId(DEFAULT_VISITOR_ID)
                .setDayOfYear(OTHER_VALID_DATE)
                .setPassType(FULLDAY_PASS_TYPE)
                .build();

        adminService.addTicket(request, responseObserver);

        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
        Assert.assertEquals(1, tickets.size());
        Assert.assertEquals(2, tickets.get(DEFAULT_VISITOR_ID).size());
        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(OTHER_VALID_DATE, formatter)));
    }

}
