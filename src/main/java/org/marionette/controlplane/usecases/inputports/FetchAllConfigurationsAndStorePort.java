package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchAllConfigsRequest;

/**
 * Port interface for fetching external configurations.
 * Defines the contract for the fetch external config use case.
 */
public interface FetchAllConfigurationsAndStorePort {
   
    public void fetchAllConfigurationsAndStore(FetchAllConfigsRequest request);

}
