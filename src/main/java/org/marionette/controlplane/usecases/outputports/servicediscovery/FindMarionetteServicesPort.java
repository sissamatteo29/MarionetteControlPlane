package org.marionette.controlplane.usecases.outputports.servicediscovery;

import java.util.List;

public interface FindMarionetteServicesPort {

    public List<DiscoveredMarionetteServiceData> findAllMarionetteServices();
    
}
