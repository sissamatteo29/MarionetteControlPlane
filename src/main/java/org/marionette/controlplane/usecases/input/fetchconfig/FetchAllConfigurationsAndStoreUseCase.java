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
            System.out.println("Contacting the service " + serviceEndpoint + "/api/getConfiguration");
            DiscoveredServiceConfigResult serviceConfigResult = nodeConfigGateway.fetchConfiguration(serviceEndpoint + "/api/getConfiguration");
            if(serviceConfigResult.isSuccessfull()) {
                AddServiceConfigRequest addServiceConfigRequest = new AddServiceConfigRequest(serviceConfigResult.serviceConfigData());
                addServiceConfigPort.execute(addServiceConfigRequest);
            } else {
                System.out.println("There was an error trying to contact the service " + serviceEndpoint + "/api/getConfiguration");
                System.out.println(serviceConfigResult.errorMessage());
            }
        }

    }
    
}
