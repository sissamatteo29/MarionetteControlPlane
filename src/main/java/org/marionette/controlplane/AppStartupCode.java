package org.marionette.controlplane;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.marionette.controlplane.adapters.output.fetchconfig.parsing.XMLParser;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.mapping.MarionetteConfigMapper;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.usecases.input.DiscoverServicesPort;
import org.marionette.controlplane.usecases.input.FetchAllConfigurationsAndStorePort;
import org.marionette.controlplane.usecases.input.addserviceconfig.DomainServiceConfigFactory;
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
        
    }


    private void devSetup() throws Exception {

        // Read marionette file
        File marionetteFile = new File("marionette.xml");

        if (!marionetteFile.exists()) {
            System.out.println("marionette.xml not found, using sample data...");
            throw new RuntimeException();
        }

        String xmlContent = Files.readString(marionetteFile.toPath(), StandardCharsets.UTF_8);

        System.out.println("Read XML file, size: " + xmlContent.length() + " characters");

        // Parse the XML string
        MarionetteConfigDTO marionetteConfig = XMLParser.parseFromXMLString(xmlContent);
        ServiceConfig serviceConfig = DomainServiceConfigFactory.createServiceConfig(
            MarionetteConfigMapper.toDomainServiceConfigData(marionetteConfig)
        );

        globalRegistry.addServiceConfig(serviceConfig.getServiceName(), serviceConfig);

        System.out.println("=== ConfigRegistry populated successfully ===");
        System.out.println(globalRegistry.toString());
        System.out.println(globalRegistry);
    }

}
