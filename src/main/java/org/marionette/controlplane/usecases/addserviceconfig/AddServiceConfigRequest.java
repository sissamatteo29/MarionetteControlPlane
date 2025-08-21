package org.marionette.controlplane.usecases.addserviceconfig;

import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.values.ServiceName;

import static java.util.Objects.requireNonNull;

public class AddServiceConfigRequest {

    private final ServiceName serviceName;
    private final ServiceConfig serviceConfig;

    public AddServiceConfigRequest(ServiceName serviceName, ServiceConfig serviceConfig) {
        requireNonNull(serviceName, "The service name cannot be null in an AddServiceConfigRequest");
        requireNonNull(serviceConfig, "The service configuration cannot be null in an AddServiceConfigRequest");

        this.serviceName = serviceName;
        this.serviceConfig = ServiceConfig.copyOf(serviceConfig);   // Defensive copy in 
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public ServiceConfig getServiceConfig() {
        return ServiceConfig.copyOf(serviceConfig);     // Defensive copy out
    }
    
}
