package org.marionette.controlplane.usecases.input.fetchconfig;

import org.marionette.controlplane.usecases.input.AddServiceConfigPort;
import org.marionette.controlplane.usecases.input.FetchAllConfigurationsAndStorePort;
import org.marionette.controlplane.usecases.input.addserviceconfig.AddServiceConfigRequest;
import org.marionette.controlplane.usecases.output.fetchconfig.DiscoveredServiceConfigResult;
import org.marionette.controlplane.usecases.output.fetchconfig.NodeConfigGateway;


public class FetchAllConfigurationsAndStoreUseCase implements FetchAllConfigurationsAndStorePort {

    private final AddServiceConfigPort addServiceConfigPort;
    private final NodeConfigGateway nodeConfigGateway;

    public FetchAllConfigurationsAndStoreUseCase(AddServiceConfigPort addServiceConfigPort, NodeConfigGateway nodeConfigGateway) {
        this.addServiceConfigPort = addServiceConfigPort;
        this.nodeConfigGateway = nodeConfigGateway;
    }

    @Override
    public void fetchAllConfigurationsAndStore(FetchAllConfigsRequest request) {

        for(String serviceEndpoint : request.serviceEndpoints()) {
            DiscoveredServiceConfigResult serviceConfigResult = nodeConfigGateway.fetchConfiguration(serviceEndpoint);
            AddServiceConfigRequest addServiceConfigRequest = new AddServiceConfigRequest(serviceConfigResult.serviceConfigData());
            addServiceConfigPort.execute(addServiceConfigRequest);
        }

    }

    
}
