package org.marionette.controlplane.usecases.outputports.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.domain.DiscoveredMarionetteServiceMetadata;

public interface FindMarionetteServicesPort {

    public List<DiscoveredMarionetteServiceMetadata> findAllMarionetteServices();
    
}
