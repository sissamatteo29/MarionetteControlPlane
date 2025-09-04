package org.marionette.controlplane.usecases.inbound.abntest.ranking;

import org.marionette.controlplane.usecases.outbound.fetchmetrics.OrderedMetricsMetadataProvider;

public class SystemConfigurationsRanker {

    private final OrderedMetricsMetadataProvider metricsMetadataProvider;
    private final SystemMetricsAggregator systemMetricsAggregator;

    public SystemConfigurationsRanker(OrderedMetricsMetadataProvider metricsMetadataProvider, SystemMetricsAggregator systemMetricsAggregator) {
        this.metricsMetadataProvider = metricsMetadataProvider;
        this.systemMetricsAggregator = systemMetricsAggregator;
    }
    
}
