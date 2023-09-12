package ar.edu.itba.pod.grpc.server.services;

import ar.edu.itba.pod.grpc.*;
import ar.edu.itba.pod.grpc.server.handlers.AttractionHandler;
import ar.edu.itba.pod.grpc.server.results.SuggestedCapacityResult;
import ar.edu.itba.pod.grpc.server.utils.ParseUtils;
import io.grpc.stub.StreamObserver;

import java.util.Collection;

public class QueryServiceImpl extends QueryServiceGrpc.QueryServiceImplBase {
    private final AttractionHandler attractionHandler;

    public QueryServiceImpl(AttractionHandler attractionHandler) {
        this.attractionHandler = attractionHandler;
    }

    @Override
    public void getSuggestedCapacities(DayOfYearRequest request, StreamObserver<SuggestedCapacitiesResponse> responseObserver) {
        int dayOfYear = ParseUtils.checkValidDayOfYear(request.getDayOfYear());

        SuggestedCapacitiesResponse.Builder responseBuilder = SuggestedCapacitiesResponse.newBuilder();

        Collection<SuggestedCapacityResult> results = attractionHandler.getSuggestedCapacities(dayOfYear);
        for (SuggestedCapacityResult result : results) {
            responseBuilder.addSuggestedCapacity(SuggestedCapacity.newBuilder()
                    .setAttractionName(result.attraction().getName())
                    .setMaxPendingReservations(result.maxPendingReservationCount())
                    .setSlotWithMaxReservations(ParseUtils.formatTime(result.slotTime()))
                    .build());
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getConfirmedReservations(DayOfYearRequest request, StreamObserver<ConfirmedReservationsResponse> responseObserver) {
        super.getConfirmedReservations(request, responseObserver);
        // TODO: Implement
    }
}
