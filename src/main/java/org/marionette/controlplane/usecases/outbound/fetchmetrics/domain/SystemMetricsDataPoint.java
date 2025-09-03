package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

import java.util.List;

import org.marionette.controlplane.usecases.domain.ConfigRegistrySnapshot;

public record SystemMetricsDataPoint (ConfigRegistrySnapshot systemConfigSnapshot, List<ServiceMetricsDataPoint> serviceMetrics) {

    public SystemMetricsDataPoint {
        serviceMetrics = List.copyOf(serviceMetrics());
    }
    
}
