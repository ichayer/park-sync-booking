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

import java.util.Map;
import java.util.TreeMap;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplTest {

    private final Map<String, AttractionRequest> attractions = new TreeMap<>();
    @InjectMocks
    private final AdminServiceImpl adminService = new AdminServiceImpl(attractions);

    @Mock
    private StreamObserver<BooleanResponse> responseObserver;

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

    @BeforeEach
    public void setUp() {
        attractions.clear();
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
}
