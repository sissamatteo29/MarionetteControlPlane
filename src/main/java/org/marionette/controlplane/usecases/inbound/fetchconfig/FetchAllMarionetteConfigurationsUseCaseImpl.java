package org.marionette.controlplane.usecases.inbound.fetchconfig;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.exceptions.infrastructure.checked.FetchMarionetteConfigurationException;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.inbound.FetchAllMarionetteConfigurationsUseCase;
import org.marionette.controlplane.usecases.outbound.fetchconfig.FetchMarionetteConfigurationGateway;

import static java.util.Objects.requireNonNull;

public class FetchAllMarionetteConfigurationsUseCaseImpl implements FetchAllMarionetteConfigurationsUseCase {

    private final FetchMarionetteConfigurationGateway fetchMarionetteConfigurationGateway;

    public FetchAllMarionetteConfigurationsUseCaseImpl(FetchMarionetteConfigurationGateway fetchMarionetteConfigurationGateway) {
        
        requireNonNull(fetchMarionetteConfigurationGateway, "The gateway to access a single marionette configuration cannot be null");

        this.fetchMarionetteConfigurationGateway = fetchMarionetteConfigurationGateway;
    }

    @Override
    public FetchAllMarionetteConfigurationsResult execute(FetchAllMarionetteConfigurationsRequest marionetteServices) {
        
        List<ServiceConfigData> fetchedMarionetteConfigs = new ArrayList<>();

        for(String marionetteConfigEndpoint : marionetteServices.serviceEndpoints()) {

            try {
                ServiceConfigData fetchedConfig = fetchMarionetteConfigurationGateway.fetchMarionetteConfiguration(marionetteConfigEndpoint);
                fetchedMarionetteConfigs.add(fetchedConfig);
            } catch (FetchMarionetteConfigurationException e) {     // Handle the exception, keep going with other nodes
                System.out.println("Impossible to fetch config data for " + marionetteConfigEndpoint + ". Continuing...");
            }
        
        }

        return new FetchAllMarionetteConfigurationsResult(fetchedMarionetteConfigs);
    }

    
}
