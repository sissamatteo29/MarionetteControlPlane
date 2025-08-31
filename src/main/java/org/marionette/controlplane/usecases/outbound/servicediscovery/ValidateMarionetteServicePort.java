package org.marionette.controlplane.usecases.outbound.servicediscovery;


import org.marionette.controlplane.usecases.domain.DiscoveredServiceMetadata;

public interface ValidateMarionetteServicePort {

    public boolean validateCandidateNode(DiscoveredServiceMetadata candidates);
    

}
