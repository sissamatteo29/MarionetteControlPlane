package org.marionette.controlplane.usecases.input.fetchconfig;

import java.util.List;

public record FetchAllConfigsRequest (List<String> serviceNames) {

    public FetchAllConfigsRequest {
        serviceNames = List.copyOf(serviceNames);
    }
    
}
