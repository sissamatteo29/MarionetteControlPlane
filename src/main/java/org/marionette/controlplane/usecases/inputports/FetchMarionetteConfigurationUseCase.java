package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchMarionetteConfigurationRequest;
import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchMarionetteConfigurationResult;

public interface FetchMarionetteConfigurationUseCase {

    public FetchMarionetteConfigurationResult execute(FetchMarionetteConfigurationRequest request);
    
}
