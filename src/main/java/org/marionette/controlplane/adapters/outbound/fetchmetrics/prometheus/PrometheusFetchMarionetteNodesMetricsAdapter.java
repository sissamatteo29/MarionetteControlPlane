package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.time.Duration;
import java.util.List;

import org.marionette.controlplane.usecases.outbound.fetchmetrics.FetchMarionetteNodesMetricsGateway;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.AggregateMetric;

public class PrometheusFetchMarionetteNodesMetricsAdapter implements FetchMarionetteNodesMetricsGateway {

    private final PrometheusConfiguration config;

    PrometheusFetchMarionetteNodesMetricsAdapter(PrometheusConfiguration config) {
        this.config = config;
    }

    @Override
    public List<AggregateMetric> fetchMetricsForService(String serviceName, Duration timeSpan) {
        




    }
    
}
