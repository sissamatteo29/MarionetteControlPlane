package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.PrometheusMetricConfig;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.dto.PrometheusApiResponse;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.dto.PrometheusQueryData;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.dto.PrometheusResult;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.FetchMarionetteNodesMetricsGateway;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.AggregateMetric;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrometheusFetchMarionetteNodesMetricsAdapter implements FetchMarionetteNodesMetricsGateway {

    private final PrometheusConfiguration config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PrometheusFetchMarionetteNodesMetricsAdapter(PrometheusConfiguration config) {
        this.httpClient = createHttpClient();
        this.config = config;
        this.objectMapper = new ObjectMapper();
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
            try {
                // Build the API url
                String query = PrometheusQueryBuilder.buildAggregationQuery(
                        config.getUrl(),
                        config.getApiPath(),
                        serviceName,
                        metricConfig,
                        timeSpan);

                // Fire the query
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(query))
                        .timeout(Duration.ofSeconds(20))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Parse JSON response
                    TypeReference<PrometheusApiResponse<PrometheusQueryData>> typeRef = new TypeReference<PrometheusApiResponse<PrometheusQueryData>>() {
                    };

                    PrometheusApiResponse<PrometheusQueryData> apiResponse = objectMapper.readValue(response.body(),
                            typeRef);

                    if ("success".equals(apiResponse.getStatus())) {
                        // Convert Prometheus data to your domain objects
                        AggregateMetric metric = convertToAggregateMetric(
                                apiResponse.getData(), metricConfig);
                        if (metric != null) {
                            metrics.add(metric);
                        }
                    } else {
                        // Handle error response
                        System.err.println("Prometheus API error: " + apiResponse.getError());
                    }
                } else {
                    System.err.println("HTTP error: " + response.statusCode());
                }
            } catch (Exception e) {
                System.out.println("Catching exception when sending request out for service " + serviceName);
                e.printStackTrace();
            }

        }

        return metrics;

    }

    private AggregateMetric convertToAggregateMetric(PrometheusQueryData data,
            PrometheusMetricConfig config) {
        // Convert PrometheusQueryData to your AggregateMetric domain object
        // This will depend on your specific domain model

        if (data.getResult() == null || data.getResult().isEmpty()) {
            return null;
        }

        // Example conversion logic
        PrometheusResult firstResult = data.getResult().get(0);   // Take first instance

        // Log if multiple 
        if(data.getResult().size() > 1) {
            System.err.println("The response from prometheus has multiple value");
        }

        // For instant queries (vector)
        if ("vector".equals(data.getResultType()) && firstResult.getValue() != null) {
            Object[] valueArray = firstResult.getValue();
            double timestamp = ((Number) valueArray[0]).doubleValue();
            String value = (String) valueArray[1];      

            // Create your AggregateMetric object
            return new AggregateMetric(
                    config.getDisplayName(),
                    Double.parseDouble(value),
                    Instant.ofEpochSecond((long) timestamp),
                    config.getUnit());
        }

        // For range queries (matrix)
        if ("matrix".equals(data.getResultType()) && firstResult.getValues() != null) {
            System.err.println("Received a response from Prometheus of type matrix...");
        }

        return null;
    }

}
