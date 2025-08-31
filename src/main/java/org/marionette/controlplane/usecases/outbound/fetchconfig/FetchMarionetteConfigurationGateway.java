package org.marionette.controlplane.usecases.outbound.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public interface FetchMarionetteConfigurationGateway {

    public ServiceConfigData fetchMarionetteConfiguration(String completeMarionetteConfigEndpoint);

}