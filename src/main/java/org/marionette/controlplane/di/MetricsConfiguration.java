package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.input.metrics.KubernetesPrometheusDiscovery;
import org.marionette.controlplane.adapters.input.metrics.PrometheusClient;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
@EnableAsync
@EnableScheduling
public class MetricsConfiguration {

    /**
     * Enhanced Config Registry (replaces the old ConfigRegistry)
     */
    @Bean
    public ConfigRegistry configRegistry() {
        return new ConfigRegistry();
    }

    /**
     * Namespace configuration
     */
    @Bean
    public String namespace() {
        return System.getenv("KUBERNETES_NAMESPACE") != null ? 
               System.getenv("KUBERNETES_NAMESPACE") : "default";
    }

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