package org.marionette.controlplane.usecases.inputports;

import java.util.List;

import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchAllMarionetteConfigurationsResult;
import org.marionette.controlplane.usecases.outputports.servicediscovery.DiscoveredMarionetteServiceData;

public interface FetchAllMarionetteConfigurationsUseCase {

    public FetchAllMarionetteConfigurationsResult fetchAllConfigurations(List<DiscoveredMarionetteServiceData> marionetteServices);
    
}
