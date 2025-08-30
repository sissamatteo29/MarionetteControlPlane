package org.marionette.controlplane.usecases.outputports.fetchconfig;

import java.net.URI;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public interface FetchMarionetteConfigurationGateway {

    public ServiceConfigData fetchMarionetteConfiguration(URI completeMarionetteConfigEndpoint);

}