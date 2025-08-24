package org.marionette.controlplane.usecases.input.servicediscovery;

import org.marionette.controlplane.usecases.input.DiscoverServicesPort;
import org.marionette.controlplane.usecases.output.servicediscovery.FindServicesPort;

public class DiscoverServicesUseCase implements DiscoverServicesPort {

    private final FindServicesPort findServicesPort;

    public DiscoverServicesUseCase(FindServicesPort findServicesPort) {
        this.findServicesPort = findServicesPort;
    }

    @Override
    public DiscoverServicesResult findAllServices() {
    
        return new DiscoverServicesResult(findServicesPort.getAllServices());
        
    }
    
}
