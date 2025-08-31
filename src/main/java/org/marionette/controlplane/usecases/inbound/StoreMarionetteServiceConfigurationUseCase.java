package org.marionette.controlplane.usecases.inbound;

import org.marionette.controlplane.usecases.inbound.storeconfig.StoreMarionetteServiceConfigurationRequest;

public interface StoreMarionetteServiceConfigurationUseCase {

    public void execute(StoreMarionetteServiceConfigurationRequest request);
    
}
