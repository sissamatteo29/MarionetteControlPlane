package org.marionette.controlplane.usecases.addserviceconfig;

import java.util.Objects;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.addserviceconfig.request.AddServiceConfigRequest;
import org.marionette.controlplane.usecases.ports.in.AddServiceConfigPort;

public class AddServiceConfigUseCase implements AddServiceConfigPort {

    private final ConfigRegistry globalRegistry;

    public AddServiceConfigUseCase(ConfigRegistry globalRegistry) {
        this.globalRegistry = globalRegistry;
    }

    @Override
    public void handle(AddServiceConfigRequest request) {
        Objects.requireNonNull(request, "The request object cannot be null");

        globalRegistry.addServiceConfig(request.getServiceName(), request.getServiceConfig());

    }
    
}
