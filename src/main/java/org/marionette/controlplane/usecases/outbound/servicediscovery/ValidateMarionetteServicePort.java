package org.marionette.controlplane.usecases.outbound.servicediscovery;

import java.util.List;

import org.marionette.controlplane.usecases.domain.DiscoveredServiceMetadata;

public interface ValidateMarionetteServicePort {

    public List<DiscoveredServiceMetadata> filterValidMarionetteNodes(List<DiscoveredServiceMetadata> candidates);
    

}
