package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

import java.util.List;


public record SystemMetricsDataPoint (List<ServiceMetricsDataPoint> serviceMetrics) {

    public SystemMetricsDataPoint {
        serviceMetrics = List.copyOf(serviceMetrics());
    }
    
}
