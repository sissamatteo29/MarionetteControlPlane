package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.storeconfig.StoreMarionetteServiceConfigurationRequest;

public interface StoreMarionetteServiceConfigurationUseCase {

    public void execute(StoreMarionetteServiceConfigurationRequest request);
    
}
