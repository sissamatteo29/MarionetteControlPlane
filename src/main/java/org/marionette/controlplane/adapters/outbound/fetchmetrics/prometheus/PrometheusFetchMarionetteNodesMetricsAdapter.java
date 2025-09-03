package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.PrometheusMetricConfig;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.FetchMarionetteNodesMetricsGateway;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.AggregateMetric;

public class PrometheusFetchMarionetteNodesMetricsAdapter implements FetchMarionetteNodesMetricsGateway {

    private final PrometheusConfiguration config;
    private final HttpClient httpClient;

    public PrometheusFetchMarionetteNodesMetricsAdapter(PrometheusConfiguration config) {
        this.httpClient = createHttpClient();
        this.config = config;
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public List<AggregateMetric> fetchMetricsForService(String serviceName, Duration timeSpan) {

        List<AggregateMetric> metrics = new ArrayList<>();

        for (PrometheusMetricConfig metricConfig : config.getMetrics()) {

            // Build the API url
            String query = PrometheusQueryBuilder.buildAggregationQuery(
                    config.getUrl(),
                    config.getApiPath(),
                    serviceName,
                    metricConfig,
                    timeSpan);

            // Fire the query




        }

    }

}
