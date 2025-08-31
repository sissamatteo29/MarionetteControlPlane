package org.marionette.controlplane.usecases.inbound;

import org.marionette.controlplane.usecases.inbound.fetchconfig.FetchMarionetteConfigurationRequest;
import org.marionette.controlplane.usecases.inbound.fetchconfig.FetchMarionetteConfigurationResult;

public interface FetchMarionetteConfigurationUseCase {

    public FetchMarionetteConfigurationResult execute(FetchMarionetteConfigurationRequest request);
    
}
