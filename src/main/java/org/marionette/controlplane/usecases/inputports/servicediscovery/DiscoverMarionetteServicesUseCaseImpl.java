package org.marionette.controlplane.usecases.inputports.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.inputports.DiscoverMarionetteServicesUseCase;
import org.marionette.controlplane.usecases.outputports.servicediscovery.DiscoveredMarionetteServiceData;
import org.marionette.controlplane.usecases.outputports.servicediscovery.FindMarionetteServicesPort;

public class DiscoverMarionetteServicesUseCaseImpl implements DiscoverMarionetteServicesUseCase {

    private final FindMarionetteServicesPort findServicesPort;

    public DiscoverMarionetteServicesUseCaseImpl(FindMarionetteServicesPort findServicesPort) {
        this.findServicesPort = findServicesPort;
    }

    /***
     * Automatic discovery of all Marionette services. The application does not have any input data to do it.
     */
    @Override
    public DiscoverMarionetteServicesResult findAllMarionetteServices() {
    
        List<DiscoveredMarionetteServiceData> discoveredServices = findServicesPort.findAllMarionetteServices();
        return new DiscoverMarionetteServicesResult(discoveredServices);
        
    }
    
}
