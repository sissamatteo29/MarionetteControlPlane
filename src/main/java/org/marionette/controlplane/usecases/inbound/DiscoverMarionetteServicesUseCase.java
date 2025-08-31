package org.marionette.controlplane.usecases.inbound;

import org.marionette.controlplane.usecases.inbound.servicediscovery.DiscoverMarionetteServicesResult;

public interface DiscoverMarionetteServicesUseCase {

    public DiscoverMarionetteServicesResult findAllMarionetteServices();
    
}
