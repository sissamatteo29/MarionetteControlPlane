package org.marionette.controlplane.usecases.inputports.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.outputports.servicediscovery.DiscoveredMarionetteServiceData;

public record DiscoverMarionetteServicesResult (List<DiscoveredMarionetteServiceData> discoveredServices) {

    public DiscoverMarionetteServicesResult {
        discoveredServices = List.copyOf(discoveredServices);
    }

}
