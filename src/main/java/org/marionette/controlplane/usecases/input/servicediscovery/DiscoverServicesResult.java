package org.marionette.controlplane.usecases.input.servicediscovery;

import java.util.List;

public record DiscoverServicesResult (List<String> serviceNames) {

    public DiscoverServicesResult {
        serviceNames = List.copyOf(serviceNames);
    }

}
