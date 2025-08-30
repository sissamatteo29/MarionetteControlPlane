package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.input.changeconfig.ChangeConfigService;
import org.marionette.controlplane.adapters.input.controllers.ConfigurationController;
import org.marionette.controlplane.adapters.output.fetchconfig.NodeConfigAdapter;
import org.marionette.controlplane.adapters.output.servicediscovery.KubernetesFindServicesAdapter;
import org.marionette.controlplane.adapters.output.servicediscovery.ServiceDiscoveryService;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.inputports.AddServiceConfigPort;
import org.marionette.controlplane.usecases.inputports.DiscoverMarionetteServicesUseCase;
import org.marionette.controlplane.usecases.inputports.FetchAllConfigurationsAndStorePort;
import org.marionette.controlplane.usecases.inputports.addserviceconfig.AddServiceConfigUseCase;
import org.marionette.controlplane.usecases.inputports.fetchconfig.FetchAllConfigurationsAndStoreUseCase;
import org.marionette.controlplane.usecases.inputports.servicediscovery.DiscoverMarionetteServicesUseCaseImpl;
import org.marionette.controlplane.usecases.outputports.fetchconfig.NodeConfigGateway;
import org.marionette.controlplane.usecases.outputports.servicediscovery.FindMarionetteServicesPort;
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
    public AddServiceConfigPort createAddServiceConfigPort(ConfigRegistry globalRegistry) {
        return new AddServiceConfigUseCase(globalRegistry);
    }

    @Bean
    public FindMarionetteServicesPort createFindServicesPort() {
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
    public DiscoverMarionetteServicesUseCase createDiscoverServicesPort(FindMarionetteServicesPort findServicesPort) {
        return new DiscoverMarionetteServicesUseCase(findServicesPort);
    }

    @Bean
    public NodeConfigGateway createNodeConfigGateway() {
        return new NodeConfigAdapter();
    }

    @Bean
    public FetchAllConfigurationsAndStorePort createFetchAllConfigurationsAndStorePort(
            NodeConfigGateway nodeConfigGateway, AddServiceConfigPort addServiceConfigPort) {
        return new FetchAllConfigurationsAndStoreUseCase(addServiceConfigPort, nodeConfigGateway);

    };

}
