package org.marionette.controlplane.usecases.inbound.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.domain.DiscoveredServiceMetadata;

public record DiscoverMarionetteServicesResult (List<DiscoveredServiceMetadata> discoveredServices) {

    public DiscoverMarionetteServicesResult {
        discoveredServices = List.copyOf(discoveredServices);
    }

}
