package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.inbound.changeconfig.ChangeConfigService;
import org.marionette.controlplane.adapters.inbound.controllers.ConfigurationController;
import org.marionette.controlplane.adapters.outbound.fetchconfig.NodeConfigAdapter;
import org.marionette.controlplane.adapters.outbound.servicediscovery.KubernetesFindServicesAdapter;
import org.marionette.controlplane.adapters.outbound.servicediscovery.ServiceDiscoveryService;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.inbound.DiscoverMarionetteServicesUseCase;
import org.marionette.controlplane.usecases.inbound.FetchAllConfigurationsAndStorePort;
import org.marionette.controlplane.usecases.inbound.StoreMarionetteServiceConfigurationUseCase;
import org.marionette.controlplane.usecases.inbound.fetchconfig.FetchAllConfigurationsAndStoreUseCase;
import org.marionette.controlplane.usecases.inbound.servicediscovery.DiscoverMarionetteServicesUseCaseImpl;
import org.marionette.controlplane.usecases.inbound.storeconfig.StoreMarionetteServiceConfigurationUseCaseImpl;
import org.marionette.controlplane.usecases.outbound.fetchconfig.FetchMarionetteConfigurationGateway;
import org.marionette.controlplane.usecases.outbound.servicediscovery.FindCandidateServicesPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ApplicationConfiguration {

    @Bean
    @Scope("singleton")
    public ConfigRegistry configRegistry() {
        return new ConfigRegistry();
    }

    @Bean
    public StoreMarionetteServiceConfigurationUseCase createAddServiceConfigPort(ConfigRegistry globalRegistry) {
        return new StoreMarionetteServiceConfigurationUseCaseImpl(globalRegistry);
    }

    @Bean
    public FindCandidateServicesPort createFindServicesPort() {
        String namespace = System.getenv("KUBERNETES_NAMESPACE");
        if (namespace == null || namespace.trim().isEmpty()) {
            // Fallback to default namespace if environment variable is not set
            namespace = "default";
            // Optional: log a warning about using default namespace
        }
        return new KubernetesFindServicesAdapter(namespace);
    }

    @Bean 
    public ConfigurationController configurationController(ConfigRegistry configRegistry, ChangeConfigService changeConfigService, ServiceDiscoveryService discoveryService) {
        String namespace = System.getenv("KUBERNETES_NAMESPACE");
        if (namespace == null || namespace.trim().isEmpty()) {
            // Fallback to default namespace if environment variable is not set
            namespace = "default";
            // Optional: log a warning about using default namespace
        }
        return new ConfigurationController(configRegistry, namespace, changeConfigService, discoveryService);
    }

    @Bean
    public DiscoverMarionetteServicesUseCase createDiscoverServicesPort(FindCandidateServicesPort findServicesPort) {
        return new DiscoverMarionetteServicesUseCase(findServicesPort);
    }

    @Bean
    public FetchMarionetteConfigurationGateway createNodeConfigGateway() {
        return new NodeConfigAdapter();
    }

    @Bean
    public FetchAllConfigurationsAndStorePort createFetchAllConfigurationsAndStorePort(
            FetchMarionetteConfigurationGateway nodeConfigGateway, StoreMarionetteServiceConfigurationUseCase addServiceConfigPort) {
        return new FetchAllConfigurationsAndStoreUseCase(addServiceConfigPort, nodeConfigGateway);

    };

}
