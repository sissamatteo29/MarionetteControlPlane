package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.input.metrics.PrometheusClient;
import org.marionette.controlplane.adapters.input.metrics.KubernetesPrometheusDiscovery;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
public class MetricsConfiguration {

    /**
     * RestTemplate configured for Prometheus API calls
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * ObjectMapper for JSON parsing
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Kubernetes service discovery for Prometheus
     */
    @Bean
    public KubernetesPrometheusDiscovery kubernetesPrometheusDiscovery(
            RestTemplate restTemplate, ObjectMapper objectMapper) {
        return new KubernetesPrometheusDiscovery(restTemplate, objectMapper);
    }

    /**
     * Prometheus client for metrics collection
     */
    @Bean
    public PrometheusClient prometheusClient(RestTemplate restTemplate, 
                                           ObjectMapper objectMapper,
                                           KubernetesPrometheusDiscovery prometheusDiscovery) {
        return new PrometheusClient(restTemplate, objectMapper, prometheusDiscovery);
    }
}