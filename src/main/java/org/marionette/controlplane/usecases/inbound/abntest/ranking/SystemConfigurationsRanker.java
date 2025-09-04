package org.marionette.controlplane.usecases.inbound.abntest.ranking;

import org.marionette.controlplane.usecases.outbound.fetchmetrics.OrderedMetricsMetadataProvider;

public class SystemConfigurationsRanker {

    private final OrderedMetricsMetadataProvider metricsMetadataProvider;

    public SystemConfigurationsRanker(OrderedMetricsMetadataProvider metricsMetadataProvider) {
        this.metricsMetadataProvider = metricsMetadataProvider;
    }
    
}
