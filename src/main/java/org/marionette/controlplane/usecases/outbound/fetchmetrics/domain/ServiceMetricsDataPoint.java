package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

import java.util.List;

import org.marionette.controlplane.usecases.domain.configsnapshot.ServiceSnapshot;

public record ServiceMetricsDataPoint (ServiceSnapshot serviceConfiguration, List<AggregateMetric> metrics) {

    public ServiceMetricsDataPoint {
        metrics = List.copyOf(metrics());
    }
}
