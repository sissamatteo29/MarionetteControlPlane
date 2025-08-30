package org.marionette.controlplane.usecases.inputports.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.inputports.FetchMarionetteConfigurationUseCase;
import org.marionette.controlplane.usecases.outputports.fetchconfig.FetchMarionetteConfigurationGateway;

import static java.util.Objects.requireNonNull;

public class FetchMarionetteConfigurationUseCaseImpl implements FetchMarionetteConfigurationUseCase {

    // Global config exposed through env vars
    private final FetchMarionetteConfigurationGateway fetchMarionetteConfigurationGateway;

    public FetchMarionetteConfigurationUseCaseImpl(FetchMarionetteConfigurationGateway fetchMarionetteConfigurationGateway) {
        requireNonNull(fetchMarionetteConfigurationGateway, "The node config cannot be null");
        this.fetchMarionetteConfigurationGateway = fetchMarionetteConfigurationGateway;
    }

    @Override
    public FetchMarionetteConfigurationResult execute(FetchMarionetteConfigurationRequest request) {
        ServiceConfigData fetchedMarionetteConfig = fetchMarionetteConfigurationGateway.fetchMarionetteConfiguration(request.serviceEndpoint());
        return new FetchMarionetteConfigurationResult(fetchedMarionetteConfig);
    }
    
}
