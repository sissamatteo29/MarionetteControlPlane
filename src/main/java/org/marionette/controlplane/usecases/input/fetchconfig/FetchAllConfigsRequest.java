package org.marionette.controlplane.usecases.input.fetchconfig;

import java.util.List;

public record FetchAllConfigsRequest (List<String> serviceEndpoints) {

    public FetchAllConfigsRequest {
        serviceEndpoints = List.copyOf(serviceEndpoints);
    }
    
}
