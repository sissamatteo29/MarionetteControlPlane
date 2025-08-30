package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchAllMarionetteConfigurationsRequest;
import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchAllMarionetteConfigurationsResult;

public interface FetchAllMarionetteConfigurationsUseCase {

    public FetchAllMarionetteConfigurationsResult execute(FetchAllMarionetteConfigurationsRequest marionetteServices);
    
}
