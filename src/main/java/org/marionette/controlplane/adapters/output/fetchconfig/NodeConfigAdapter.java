package org.marionette.controlplane.adapters.output.fetchconfig;

import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteServiceConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.mapping.MarionetteConfigMapper;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.output.fetchconfig.DiscoveredServiceConfigResult;
import org.marionette.controlplane.usecases.output.fetchconfig.NodeConfigGateway;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NodeConfigAdapter implements NodeConfigGateway {

    private final HttpClient httpClient;

    public NodeConfigAdapter() {        
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
    public DiscoveredServiceConfigResult fetchConfiguration(String serviceEndpoint) {
        try {
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceEndpoint))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(response.body());

            if (response.statusCode() == 200) {

                System.out.println("Obtained config from endpoint " + serviceEndpoint);
                System.out.println("### PRINTING CONFIG ###");
                System.out.println(response.body());

                ObjectMapper mapper = new ObjectMapper();
                MarionetteServiceConfigDTO marionetteServiceConfigDTO = mapper.readValue(response.body(), MarionetteServiceConfigDTO.class);

                // Map
                ServiceConfigData serviceConfigData = MarionetteConfigMapper.toDomainServiceConfigData(marionetteServiceConfigDTO);
                
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
