package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.output.fetchconfig.NodeConfigAdapter;
import org.marionette.controlplane.adapters.output.servicediscovery.KubernetesFindServicesAdapter;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.input.AddServiceConfigPort;
import org.marionette.controlplane.usecases.input.DiscoverServicesPort;
import org.marionette.controlplane.usecases.input.FetchAllConfigurationsAndStorePort;
import org.marionette.controlplane.usecases.input.addserviceconfig.AddServiceConfigUseCase;
import org.marionette.controlplane.usecases.input.fetchconfig.FetchAllConfigurationsAndStoreUseCase;
import org.marionette.controlplane.usecases.input.servicediscovery.DiscoverServicesUseCase;
import org.marionette.controlplane.usecases.output.fetchconfig.NodeConfigGateway;
import org.marionette.controlplane.usecases.output.servicediscovery.FindServicesPort;
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
    public FindServicesPort createFindServicesPort() {
        return new KubernetesFindServicesAdapter(""); // namespace
    }

    @Bean
    public DiscoverServicesPort createDiscoverServicesPort(FindServicesPort findServicesPort) {
        return new DiscoverServicesUseCase(findServicesPort);
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
