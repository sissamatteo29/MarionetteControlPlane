package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

import java.util.List;

public class MetricsConfiguration {

    private final List<OrderedMetricMetadata> metricsConfig;

    public MetricsConfiguration(List<OrderedMetricMetadata> metricsConfig) {
        this.metricsConfig = List.copyOf(metricsConfig);
    }

    public List<OrderedMetricMetadata> getMetricsConfig() {
        return metricsConfig;
    }

    
}
