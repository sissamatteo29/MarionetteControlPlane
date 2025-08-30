package org.marionette.controlplane.usecases.outputports.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.outputports.servicediscovery.DiscoveredMarionetteServiceData;

public interface NodeConfigGateway {

    public ServiceConfigData fetchMarionetteConfiguration(DiscoveredMarionetteServiceData discoveredServiceData);

}