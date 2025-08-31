package org.marionette.controlplane.usecases.inbound;

import org.marionette.controlplane.usecases.inbound.fetchconfig.FetchAllMarionetteConfigurationsRequest;
import org.marionette.controlplane.usecases.inbound.fetchconfig.FetchAllMarionetteConfigurationsResult;

public interface FetchAllMarionetteConfigurationsUseCase {

    public FetchAllMarionetteConfigurationsResult execute(FetchAllMarionetteConfigurationsRequest marionetteServices);
    
}
