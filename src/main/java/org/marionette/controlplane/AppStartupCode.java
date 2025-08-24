package org.marionette.controlplane;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.input.DiscoverServicesPort;
import org.marionette.controlplane.usecases.input.FetchAllConfigurationsAndStorePort;
import org.marionette.controlplane.usecases.input.fetchconfig.FetchAllConfigsRequest;
import org.marionette.controlplane.usecases.input.servicediscovery.DiscoverServicesResult;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupCode implements CommandLineRunner {

    private final ConfigRegistry globalRegistry;
    private final DiscoverServicesPort discoverServices;
    private final FetchAllConfigurationsAndStorePort fetchAllConfigurations;

    public AppStartupCode(ConfigRegistry globalRegistry, DiscoverServicesPort discoverServices,
            FetchAllConfigurationsAndStorePort fetchAllConfigurations) {
        this.globalRegistry = globalRegistry;
        this.discoverServices = discoverServices;
        this.fetchAllConfigurations = fetchAllConfigurations;
    }

    @Override
    public void run(String... args) throws Exception {
        DiscoverServicesResult discoverServicesResult = discoverServices.findAllServices();
        fetchAllConfigurations.fetchAllConfigurationsAndStore(new FetchAllConfigsRequest(discoverServicesResult.serviceNames()));
        System.out.println(globalRegistry);
    }
    
}
