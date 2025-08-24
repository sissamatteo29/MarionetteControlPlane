package org.marionette.controlplane.usecases.input;

import org.marionette.controlplane.usecases.input.servicediscovery.DiscoverServicesResult;

public interface DiscoverServicesPort {

    public DiscoverServicesResult findAllServices();
    
}
