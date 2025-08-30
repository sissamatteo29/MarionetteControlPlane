package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.servicediscovery.DiscoverMarionetteServicesResult;

public interface DiscoverMarionetteServicesUseCase {

    public DiscoverMarionetteServicesResult findAllMarionetteServices();
    
}
