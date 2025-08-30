package org.marionette.controlplane.usecases.inputports.fetchconfig;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.inputports.FetchAllMarionetteConfigurationsUseCase;
import org.marionette.controlplane.usecases.inputports.FetchMarionetteConfigurationUseCase;

import static java.util.Objects.requireNonNull;

public class FetchAllMarionetteConfigurationsUseCaseImpl implements FetchAllMarionetteConfigurationsUseCase {

    private final FetchMarionetteConfigurationUseCase fetchSingleConfigurationUseCase;

    public FetchAllMarionetteConfigurationsUseCaseImpl(FetchMarionetteConfigurationUseCase fetchSingleConfiguraitonUseCase) {
        
        requireNonNull(fetchSingleConfiguraitonUseCase, "The use case to fetch a single Marionette configuration cannot be null");

        this.fetchSingleConfigurationUseCase = fetchSingleConfiguraitonUseCase;
    }

    @Override
    public FetchAllMarionetteConfigurationsResult execute(FetchAllMarionetteConfigurationsRequest marionetteServices) {
        
        List<ServiceConfigData> fetchedMarionetteConfigs = new ArrayList<>();

        for(String marionetteConfigEndpoint : marionetteServices.completeMarionetteConfigEndpoints()) {

            fetchedMarionetteConfigs.add(
                fetchSingleConfigurationUseCase.execute(new FetchMarionetteConfigurationRequest(marionetteConfigEndpoint)).serviceData()
            );
        }

        return new FetchAllMarionetteConfigurationsResult(fetchedMarionetteConfigs);
    }



    
}
