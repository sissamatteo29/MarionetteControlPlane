package org.marionette.controlplane.adapters.output.fetchconfig;

import org.marionette.controlplane.adapters.output.fetchconfig.uri.ServiceURIFactory;
import org.marionette.controlplane.adapters.output.parsing.XMLParser;
import org.marionette.controlplane.adapters.output.parsing.dto.MarionetteConfigDTO;
import org.marionette.controlplane.adapters.output.parsing.mapping.MarionetteConfigMapper;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.output.fetchconfig.DiscoveredServiceConfigResult;
import org.marionette.controlplane.usecases.output.fetchconfig.NodeConfigGateway;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NodeConfigAdapter implements NodeConfigGateway {

    private final ServiceURIFactory uriFactory;
    private final HttpClient httpClient;

    public NodeConfigAdapter(ServiceURIFactory uriFactory) {
        requireNonNull(uriFactory, "The uri factory cannot be null");
        
        this.uriFactory = uriFactory;
        this.httpClient = createHttpClient();
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    @Override
    public DiscoveredServiceConfigResult fetchConfiguration(String serviceName) {
        try {
            URI resourceURI = uriFactory.createServiceURI(serviceName);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(resourceURI)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                // Parse the XML response
                MarionetteConfigDTO configDTO = XMLParser.parseFromXMLString(response.body());

                // Map
                ServiceConfigData serviceConfigData = MarionetteConfigMapper.toDomainServiceConfigData(configDTO);
                
                return DiscoveredServiceConfigResult.success(serviceConfigData);
            } else {
                return DiscoveredServiceConfigResult.failure(
                    "HTTP " + response.statusCode()
                );
            }
            
        } catch (IOException | InterruptedException e) {
            return DiscoveredServiceConfigResult.failure(e.getMessage());
        } catch (Exception e) {
            return DiscoveredServiceConfigResult.failure(e.getMessage());
        }
    }
    
}
