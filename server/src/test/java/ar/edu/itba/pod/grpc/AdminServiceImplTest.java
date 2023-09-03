//package ar.edu.itba.pod.grpc;
//
//import ar.edu.itba.pod.grpc.server.models.Attraction;
//import ar.edu.itba.pod.grpc.server.services.AdminServiceImpl;
//import io.grpc.stub.StreamObserver;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RunWith(MockitoJUnitRunner.class)
//public class AdminServiceImplTest {
//
//    private static final String ATTRACTION_NAME = "attractionName";
//    private static final String ANOTHER_ATTRACTION_NAME = "anotherAttractionName";
//    private static final String INVALID_ATTRACTION_NAME = "";
//    private static final String HOURS_FROM = "10:00";
//    private static final String HOURS_TO = "18:00";
//    private static final String INVALID_HOURS_FROM = "25:00";
//    private static final String INVALID_HOURS_TO = "18:61";
//    private static final String INVALID_HOURS_FROM_FORMAT = "10:0";
//    private static final String INVALID_HOURS_TO_FORMAT = "10:00:00";
//    private static final int SLOT_GAP = 10;
//    private static final int NO_SLOT_GAP = 0;
//    private static final int NEGATIVE_SLOT_GAP = -1;
//    private static final int INVALID_SLOT_GAP = 61;
//    private static final PassType FULLDAY_PASS_TYPE = PassType.PASS_TYPE_FULL_DAY;
//    private static final PassType HALFDAY_PASS_TYPE = PassType.PASS_TYPE_HALF_DAY;
//    private static final String DEFAULT_VISITOR_ID = "1";
//    private static final String VALID_DATE = "10-09-2023";
//    private static final String OTHER_VALID_DATE = "11-10-2023";
//    private static final String INVALID_DATE = "32-10-2023";
//    private static final String INVALID_DATE_FORMAT = "/10/2023";
//    private static final int VALID_CAPACITY = 10;
//    private static final int INVALID_CAPACITY = -1;
//    private final Map<String, Attraction> attractions = new HashMap<>();
//    private final Map<String, Map<LocalDate, PassType>> tickets = new HashMap<>();
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//    @InjectMocks
//    private final AdminServiceImpl adminService = new AdminServiceImpl(attractions, tickets);
//    @Mock
//    private StreamObserver<BooleanResponse> booleanResponseObserver;
//    @Mock
//    private StreamObserver<CapacityResponse> capacityResponseObserver;
//
//    @BeforeEach
//    public void setUp() {
//        attractions.clear();
//        tickets.clear();
//    }
//
//    @Test
//    public void testAddAttraction() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
//    }
//
//    @Test
//    public void testAddAttractionWithExistingName() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        final Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(HOURS_FROM), LocalTime.parse(HOURS_TO), SLOT_GAP);
//
//        attractions.put(ATTRACTION_NAME, attraction);
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.containsKey(ATTRACTION_NAME));
//        Assert.assertEquals(1, attractions.size());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidName() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(INVALID_ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidSlotGapNoMinutes() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(NO_SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidSlotGapNegativeMinutes() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(NEGATIVE_SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidSlotGapMoreThan60Minutes() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(INVALID_SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidHours() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_TO)
//                .setHoursTo(HOURS_FROM)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidHourForm() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(INVALID_HOURS_FROM)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidHourTo() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(INVALID_HOURS_TO)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidHourFormFormat() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(INVALID_HOURS_FROM_FORMAT)
//                .setHoursTo(HOURS_TO)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddAttractionWithInvalidHourToFormat() {
//        final AttractionRequest request = AttractionRequest.newBuilder()
//                .setName(ATTRACTION_NAME)
//                .setHoursFrom(HOURS_FROM)
//                .setHoursTo(INVALID_HOURS_TO_FORMAT)
//                .setSlotGap(SLOT_GAP)
//                .build();
//
//        adminService.addAttraction(request, booleanResponseObserver);
//
//        Assert.assertTrue(attractions.isEmpty());
//    }
//
//    @Test
//    public void testAddTicket() {
//        TicketRequest request = TicketRequest.newBuilder()
//                .setVisitorId(DEFAULT_VISITOR_ID)
//                .setDayOfYear(VALID_DATE)
//                .setPassType(FULLDAY_PASS_TYPE)
//                .build();
//
//        adminService.addTicket(request, booleanResponseObserver);
//
//        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
//        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
//        Assert.assertEquals(1, tickets.size());
//        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID).size());
//    }
//
//    @Test
//    public void testAddTicketFailureInvalidDate() {
//        TicketRequest request = TicketRequest.newBuilder()
//                .setVisitorId(DEFAULT_VISITOR_ID)
//                .setDayOfYear(INVALID_DATE)
//                .setPassType(FULLDAY_PASS_TYPE)
//                .build();
//
//        adminService.addTicket(request, booleanResponseObserver);
//
//        Assert.assertFalse(tickets.containsKey(DEFAULT_VISITOR_ID));
//        Assert.assertEquals(0, tickets.size());
//    }
//
//    @Test
//    public void testAddTicketFailureInvalidDateFormat() {
//        TicketRequest request = TicketRequest.newBuilder()
//                .setVisitorId(DEFAULT_VISITOR_ID)
//                .setDayOfYear(INVALID_DATE_FORMAT)
//                .setPassType(FULLDAY_PASS_TYPE)
//                .build();
//
//        adminService.addTicket(request, booleanResponseObserver);
//
//        Assert.assertFalse(tickets.containsKey(DEFAULT_VISITOR_ID));
//        Assert.assertEquals(0, tickets.size());
//    }
//
//    @Test
//    public void testAddSameTicketPassForSameDate() {
//        tickets.put(DEFAULT_VISITOR_ID, new HashMap<>());
//        tickets.get(DEFAULT_VISITOR_ID).put(LocalDate.parse(VALID_DATE, formatter), FULLDAY_PASS_TYPE);
//        TicketRequest request = TicketRequest.newBuilder()
//                .setVisitorId(DEFAULT_VISITOR_ID)
//                .setDayOfYear(VALID_DATE)
//                .setPassType(FULLDAY_PASS_TYPE)
//                .build();
//
//        adminService.addTicket(request, booleanResponseObserver);
//
//        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
//        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
//        Assert.assertEquals(1, tickets.size());
//        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID).size());
//    }
//
//    @Test
//    public void testAddOtherPassForSameDate() {
//        tickets.put(DEFAULT_VISITOR_ID, new HashMap<>());
//        tickets.get(DEFAULT_VISITOR_ID).put(LocalDate.parse(VALID_DATE, formatter), FULLDAY_PASS_TYPE);
//        TicketRequest request = TicketRequest.newBuilder()
//                .setVisitorId(DEFAULT_VISITOR_ID)
//                .setDayOfYear(VALID_DATE)
//                .setPassType(HALFDAY_PASS_TYPE)
//                .build();
//
//        adminService.addTicket(request, booleanResponseObserver);
//
//        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
//        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
//        Assert.assertEquals(1, tickets.size());
//        Assert.assertEquals(1, tickets.get(DEFAULT_VISITOR_ID).size());
//    }
//
//    @Test
//    public void testAddTicketForOtherDay() {
//        tickets.put(DEFAULT_VISITOR_ID, new HashMap<>());
//        tickets.get(DEFAULT_VISITOR_ID).put(LocalDate.parse(VALID_DATE, formatter), FULLDAY_PASS_TYPE);
//        TicketRequest request = TicketRequest.newBuilder()
//                .setVisitorId(DEFAULT_VISITOR_ID)
//                .setDayOfYear(OTHER_VALID_DATE)
//                .setPassType(FULLDAY_PASS_TYPE)
//                .build();
//
//        adminService.addTicket(request, booleanResponseObserver);
//
//        Assert.assertTrue(tickets.containsKey(DEFAULT_VISITOR_ID));
//        Assert.assertEquals(1, tickets.size());
//        Assert.assertEquals(2, tickets.get(DEFAULT_VISITOR_ID).size());
//        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(VALID_DATE, formatter)));
//        Assert.assertTrue(tickets.get(DEFAULT_VISITOR_ID).containsKey(LocalDate.parse(OTHER_VALID_DATE, formatter)));
//    }
//
//    @Test
//    public void testAddCapacity() {
//        CapacityRequest request = CapacityRequest.newBuilder()
//                .setAttractionName(ATTRACTION_NAME)
//                .setDayOfYear(VALID_DATE)
//                .setCapacity(VALID_CAPACITY)
//                .build();
//
//        Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(HOURS_FROM), LocalTime.parse(HOURS_TO), SLOT_GAP);
//        attractions.put(ATTRACTION_NAME, attraction);
//
//        adminService.addCapacity(request, capacityResponseObserver);
//
//        Optional<Integer> capacity = attraction.getCapacityByDate(LocalDate.parse(VALID_DATE, formatter));
//        Assert.assertTrue(capacity.isPresent());
//        Assert.assertEquals(10, capacity.get().intValue());
//    }
//
//    @Test
//    public void testAddCapacityWithOtherAttractionName() {
//        CapacityRequest request = CapacityRequest.newBuilder()
//                .setAttractionName(ANOTHER_ATTRACTION_NAME)
//                .setDayOfYear(VALID_DATE)
//                .setCapacity(VALID_CAPACITY)
//                .build();
//
//        Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(HOURS_FROM), LocalTime.parse(HOURS_TO), SLOT_GAP);
//        attractions.put(ATTRACTION_NAME, attraction);
//
//        adminService.addCapacity(request, capacityResponseObserver);
//
//        Assert.assertFalse(attraction.getCapacityByDate(LocalDate.parse(VALID_DATE, formatter)).isPresent());
//    }
//
//    @Test
//    public void testAddCapacityWithInvalidDate() {
//        CapacityRequest request = CapacityRequest.newBuilder()
//                .setAttractionName(ATTRACTION_NAME)
//                .setDayOfYear(INVALID_DATE)
//                .setCapacity(VALID_CAPACITY)
//                .build();
//
//        Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(HOURS_FROM), LocalTime.parse(HOURS_TO), SLOT_GAP);
//        attractions.put(ATTRACTION_NAME, attraction);
//
//        adminService.addCapacity(request, capacityResponseObserver);
//
//        Assert.assertEquals(0, attraction.getAmountOfDatesWithCapacitySet());
//    }
//
//    @Test
//    public void testAddCapacityWithInvalidDateFormat() {
//        CapacityRequest request = CapacityRequest.newBuilder()
//                .setAttractionName(ATTRACTION_NAME)
//                .setDayOfYear(INVALID_DATE_FORMAT)
//                .setCapacity(VALID_CAPACITY)
//                .build();
//
//        Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(HOURS_FROM), LocalTime.parse(HOURS_TO), SLOT_GAP);
//        attractions.put(ATTRACTION_NAME, attraction);
//
//        adminService.addCapacity(request, capacityResponseObserver);
//
//        Assert.assertEquals(0, attraction.getAmountOfDatesWithCapacitySet());
//    }
//
//    @Test
//    public void testAddCapacityWithInvalidCapacity() {
//        CapacityRequest request = CapacityRequest.newBuilder()
//                .setAttractionName(ATTRACTION_NAME)
//                .setDayOfYear(VALID_DATE)
//                .setCapacity(INVALID_CAPACITY)
//                .build();
//
//        Attraction attraction = new Attraction(ATTRACTION_NAME, LocalTime.parse(HOURS_FROM), LocalTime.parse(HOURS_TO), SLOT_GAP);
//        attractions.put(ATTRACTION_NAME, attraction);
//
//        adminService.addCapacity(request, capacityResponseObserver);
//
//        Assert.assertFalse(attraction.getCapacityByDate(LocalDate.parse(VALID_DATE, formatter)).isPresent());
//    }
//
//}
