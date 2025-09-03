package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusConfiguration;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusConfigurationLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ABTestingConfiguration {
    
    @Bean
    public PrometheusConfiguration loadConfiguration() {
        return PrometheusConfigurationLoader.loadFromEnv();
    }
}
