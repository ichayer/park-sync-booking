package ar.edu.itba.pod.grpc.interfaces;

import ar.edu.itba.pod.grpc.helpers.Arguments;

public interface Action {
    Action execute(Arguments arguments);

    void showResults();

}
