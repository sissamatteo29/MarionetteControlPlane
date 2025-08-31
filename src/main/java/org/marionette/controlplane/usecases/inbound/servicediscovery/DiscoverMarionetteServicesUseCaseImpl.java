package org.marionette.controlplane.usecases.inbound.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.domain.DiscoveredServiceMetadata;
import org.marionette.controlplane.usecases.inbound.DiscoverMarionetteServicesUseCase;
import org.marionette.controlplane.usecases.outbound.servicediscovery.FindCandidateServicesPort;
import org.marionette.controlplane.usecases.outbound.servicediscovery.ValidateMarionetteServicePort;

import static java.util.Objects.requireNonNull;

public class DiscoverMarionetteServicesUseCaseImpl implements DiscoverMarionetteServicesUseCase {

    private final FindCandidateServicesPort findServicesPort;
    private final ValidateMarionetteServicePort marionetteServiceValidator;

    public DiscoverMarionetteServicesUseCaseImpl(FindCandidateServicesPort findServicesPort, ValidateMarionetteServicePort marionetteServiceValidator) {

        requireNonNull(findServicesPort, "The port to find all candidate services cannot be null");
        requireNonNull(marionetteServiceValidator, "The validator for a marionette service cannot be null");
        
        this.findServicesPort = findServicesPort;
        this.marionetteServiceValidator = marionetteServiceValidator;
    }

    /***
     * Automatic discovery of all Marionette services. The application does not have any input data to do it.
     */
    @Override
    public DiscoverMarionetteServicesResult findAllMarionetteServices() {
    
        List<DiscoveredServiceMetadata> discoveredServices = findServicesPort.findCandidateServices();
        List<DiscoveredServiceMetadata> validServices = marionetteServiceValidator.filterValidMarionetteNodes(discoveredServices);

        return new DiscoverMarionetteServicesResult(validServices);
        
    }
    
}
