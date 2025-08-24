package org.marionette.controlplane.usecases.input.addserviceconfig;

import java.util.Objects;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.usecases.input.AddServiceConfigPort;

public class AddServiceConfigUseCase implements AddServiceConfigPort {

    private final ConfigRegistry globalRegistry;

    public AddServiceConfigUseCase(ConfigRegistry globalRegistry) {
        this.globalRegistry = globalRegistry;
    }

    @Override
    public void execute(AddServiceConfigRequest request) {
        Objects.requireNonNull(request, "The request object cannot be null");
        ServiceConfig serviceConfigToAdd = DomainServiceConfigFactory.createServiceConfig(request.serviceConfigData());
        globalRegistry.addServiceConfig(serviceConfigToAdd.getServiceName(), serviceConfigToAdd);
    }
    
}
