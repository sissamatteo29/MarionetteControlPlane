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

                    logPrometheusResponse(apiResponse);

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

        logResult(metrics);

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
        PrometheusResult firstResult = data.getResult().get(0); // Take first instance

        // Log if multiple
        if (data.getResult().size() > 1) {
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

    private void logPrometheusResponse(PrometheusApiResponse<PrometheusQueryData> apiResponse) {
        System.out.println("=== Prometheus API Response ===");
        System.out.println("Status: " + apiResponse.getStatus());

        if (apiResponse.getData() != null) {
            PrometheusQueryData data = apiResponse.getData();
            System.out.println("Result Type: " + data.getResultType());
            System.out.println("Number of Results: " + (data.getResult() != null ? data.getResult().size() : 0));

            if (data.getResult() != null && !data.getResult().isEmpty()) {
                System.out.println("Sample Result:");
                PrometheusResult firstResult = data.getResult().get(0);

                // Print metric labels
                if (firstResult.getMetric() != null) {
                    System.out.print("  Metric Labels: {");
                    firstResult.getMetric().forEach((key, value) -> System.out.print(key + "=" + value + " "));
                    System.out.println("}");
                }

                // Print value/values
                if (firstResult.getValue() != null) {
                    Object[] valueArray = firstResult.getValue();
                    System.out.println("  Value: [" + valueArray[0] + ", " + valueArray[1] + "]");
                }

                if (firstResult.getValues() != null) {
                    System.out.println("  Values count: " + firstResult.getValues().size());
                    if (!firstResult.getValues().isEmpty()) {
                        Object[] firstValue = firstResult.getValues().get(0);
                        System.out.println("  First value: [" + firstValue[0] + ", " + firstValue[1] + "]");
                    }
                }
            }
        }

        // Print warnings and errors if present
        if (apiResponse.getWarnings() != null && !apiResponse.getWarnings().isEmpty()) {
            System.out.println("Warnings: " + apiResponse.getWarnings());
        }

        if (apiResponse.getError() != null) {
            System.out.println("Error: " + apiResponse.getError());
            System.out.println("Error Type: " + apiResponse.getErrorType());
        }

        System.out.println("================================\n");
    }

    private void logResult(List<AggregateMetric> metrics) {
        System.out.println("=== Final Aggregate Metrics ===");
        System.out.println("Total metrics collected: " + metrics.size());

        if (metrics.isEmpty()) {
            System.out.println("No metrics were successfully converted.");
        } else {
            System.out.println("Metrics Summary:");
            for (int i = 0; i < metrics.size(); i++) {
                AggregateMetric metric = metrics.get(i);
                System.out.printf("  [%d] %s: %.2f %s (at %s)%n",
                        i + 1,
                        metric.name(),
                        metric.value(),
                        metric.unit() != null ? metric.unit() : "units",
                        metric.timestamp().toString());
            }
        }

        System.out.println("===============================\n");
    }

}
