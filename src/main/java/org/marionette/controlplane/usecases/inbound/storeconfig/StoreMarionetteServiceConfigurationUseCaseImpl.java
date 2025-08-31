package org.marionette.controlplane.usecases.inbound.storeconfig;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.usecases.domain.mappers.ServiceConfigDataMapper;
import org.marionette.controlplane.usecases.inbound.StoreMarionetteServiceConfigurationUseCase;

import static java.util.Objects.requireNonNull;

public class StoreMarionetteServiceConfigurationUseCaseImpl implements StoreMarionetteServiceConfigurationUseCase {

    private final ConfigRegistry globalRegistry;

    public StoreMarionetteServiceConfigurationUseCaseImpl(ConfigRegistry globalRegistry) {

        requireNonNull(globalRegistry, "The global registry cannot be null");

        this.globalRegistry = globalRegistry;
    }

    @Override
    public void execute(StoreMarionetteServiceConfigurationRequest request) {
        requireNonNull(request, "The request object cannot be null");
        
        // ServiceConfig is domain entity
        ServiceConfig domainServiceConfig = ServiceConfigDataMapper.toDomainServiceConfig(request.serviceConfigData());
        globalRegistry.addDiscoveredService(domainServiceConfig.getServiceName(), domainServiceConfig, request.serviceEndpoint());
    }
    
}
