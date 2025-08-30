package org.marionette.controlplane.usecases.inputports.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.domain.DiscoveredMarionetteServiceMetadata;

public record DiscoverMarionetteServicesResult (List<DiscoveredMarionetteServiceMetadata> discoveredServices) {

    public DiscoverMarionetteServicesResult {
        discoveredServices = List.copyOf(discoveredServices);
    }

}
