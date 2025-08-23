package org.marionette.controlplane.usecases.addserviceconfig;

import java.util.Objects;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.usecases.addserviceconfig.request.AddServiceConfigRequest;
import org.marionette.controlplane.usecases.ports.in.AddServiceConfigPort;

public class AddServiceConfigUseCase implements AddServiceConfigPort {

    private final ConfigRegistry globalRegistry;
    private final ServiceConfigFactory serviceConfigFactory;

    public AddServiceConfigUseCase(ConfigRegistry globalRegistry, ServiceConfigFactory serviceConfigFactory) {
        this.globalRegistry = globalRegistry;
        this.serviceConfigFactory = serviceConfigFactory;
    }

    @Override
    public void handle(AddServiceConfigRequest request) {
        Objects.requireNonNull(request, "The request object cannot be null");
        ServiceConfig serviceConfigToAdd = serviceConfigFactory.createServiceConfig(request.serviceName(), request.classConfigData());
        globalRegistry.addServiceConfig(serviceConfigToAdd.getServiceName(), serviceConfigToAdd);
    }
    
}
