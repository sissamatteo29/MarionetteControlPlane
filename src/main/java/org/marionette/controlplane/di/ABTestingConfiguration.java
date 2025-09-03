package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusConfiguration;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusConfigurationLoader;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusFetchMarionetteNodesMetricsAdapter;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.FetchMarionetteNodesMetricsGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ABTestingConfiguration {
    
    @Bean
    public PrometheusConfiguration loadConfiguration() {
        return PrometheusConfigurationLoader.loadFromEnv();
    }

    @Bean
    public FetchMarionetteNodesMetricsGateway fetchMarionetteNodesMetricsAdapter(PrometheusConfiguration config) {
        return new PrometheusFetchMarionetteNodesMetricsAdapter(config);
    }
}
