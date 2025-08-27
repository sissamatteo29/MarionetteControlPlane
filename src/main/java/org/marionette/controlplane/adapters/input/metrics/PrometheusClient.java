package org.marionette.controlplane.adapters.input.metrics;

import org.marionette.controlplane.adapters.input.metrics.TimeSeriesDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class PrometheusClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KubernetesPrometheusDiscovery prometheusDiscovery;
    
    @Value("${prometheus.url:}")
    private String configuredPrometheusUrl;
    
    private String prometheusBaseUrl;

    public PrometheusClient(RestTemplate restTemplate, ObjectMapper objectMapper, 
                           KubernetesPrometheusDiscovery prometheusDiscovery) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.prometheusDiscovery = prometheusDiscovery;
    }

    @PostConstruct
    public void initializePrometheusUrl() {
        if (configuredPrometheusUrl != null && !configuredPrometheusUrl.isEmpty()) {
            prometheusBaseUrl = configuredPrometheusUrl;
        } else {
            // Auto-discover Prometheus in the cluster
            prometheusBaseUrl = prometheusDiscovery.discoverPrometheus();
        }
        
        if (prometheusBaseUrl == null) {
            throw new RuntimeException("Could not find Prometheus instance. Please configure prometheus.url or ensure Prometheus is running in the cluster.");
        }
        
        System.out.println("Using Prometheus at: " + prometheusBaseUrl);
    }

    /**
     * Query Prometheus for range data
     */
    public List<TimeSeriesDataDTO> queryRange(String query, Instant startTime, Instant endTime, String step) {
        try {
            String url = String.format("%s/api/v1/query_range?query=%s&start=%s&end=%s&step=%s",
                prometheusBaseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8),
                startTime.getEpochSecond(),
                endTime.getEpochSecond(),
                step
            );

            String response = restTemplate.getForObject(url, String.class);
            return parsePrometheusResponse(response);
            
        } catch (HttpClientErrorException e) {
            System.err.println("Error querying Prometheus: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Query for histogram percentiles (e.g., 95th percentile response time)
     */
    public List<TimeSeriesDataDTO> queryRangeHistogramPercentile(String metricName, String percentile, 
                                                                String serviceName, Instant startTime, 
                                                                Instant endTime, String step) {
        String query = String.format("histogram_quantile(%s, rate(%s_bucket{service=\"%s\"}[1m]))", 
                                    percentile, metricName, serviceName);
        return queryRange(query, startTime, endTime, step);
    }

    /**
     * Query for request rate
     */
    public List<TimeSeriesDataDTO> queryRangeRate(String metricName, String serviceName, 
                                                 Instant startTime, Instant endTime, String step) {
        String query = String.format("rate(%s{service=\"%s\"}[1m])", metricName, serviceName);
        return queryRange(query, startTime, endTime, step);
    }

    /**
     * Query for error rate
     */
    public List<TimeSeriesDataDTO> queryRangeErrorRate(String metricName, String serviceName, 
                                                      Instant startTime, Instant endTime, String step) {
        String query = String.format(
            "rate(%s{service=\"%s\",status=~\"4..|5..\"}[1m]) / rate(%s{service=\"%s\"}[1m])", 
            metricName, serviceName, metricName, serviceName
        );
        return queryRange(query, startTime, endTime, step);
    }

    /**
     * Query with method filter for method-specific metrics
     */
    public List<TimeSeriesDataDTO> queryRangeWithMethodFilter(String metricName, String serviceName, 
                                                             String methodName, Instant startTime, 
                                                             Instant endTime, String step) {
        String query = String.format("histogram_quantile(0.95, rate(%s_bucket{service=\"%s\",method=\"%s\"}[1m]))", 
                                    metricName, serviceName, methodName);
        return queryRange(query, startTime, endTime, step);
    }

    /**
     * Query method-specific request rate
     */
    public List<TimeSeriesDataDTO> queryRangeRateWithMethodFilter(String metricName, String serviceName, 
                                                                 String methodName, Instant startTime, 
                                                                 Instant endTime, String step) {
        String query = String.format("rate(%s{service=\"%s\",method=\"%s\"}[1m])", 
                                    metricName, serviceName, methodName);
        return queryRange(query, startTime, endTime, step);
    }

    /**
     * Get current metric values (instant query)
     */
    public Map<String, Double> getCurrentMetrics(String serviceName) {
        Map<String, Double> metrics = new HashMap<>();
        
        try {
            // Current response time (95th percentile)
            String responseTimeQuery = String.format(
                "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket{service=\"%s\"}[1m]))", 
                serviceName
            );
            Double responseTime = queryInstant(responseTimeQuery);
            if (responseTime != null) metrics.put("responseTime", responseTime);

            // Current request rate
            String requestRateQuery = String.format("rate(http_requests_total{service=\"%s\"}[1m])", serviceName);
            Double requestRate = queryInstant(requestRateQuery);
            if (requestRate != null) metrics.put("requestRate", requestRate);

            // Current error rate
            String errorRateQuery = String.format(
                "rate(http_requests_total{service=\"%s\",status=~\"4..|5..\"}[1m]) / rate(http_requests_total{service=\"%s\"}[1m])", 
                serviceName, serviceName
            );
            Double errorRate = queryInstant(errorRateQuery);
            if (errorRate != null) metrics.put("errorRate", errorRate);

        } catch (Exception e) {
            System.err.println("Error fetching current metrics: " + e.getMessage());
        }

        return metrics;
    }

    private Double queryInstant(String query) {
        try {
            String url = String.format("%s/api/v1/query?query=%s",
                prometheusBaseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8)
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.path("data").path("result");
            
            if (result.isArray() && result.size() > 0) {
                JsonNode value = result.get(0).path("value");
                if (value.isArray() && value.size() > 1) {
                    return value.get(1).asDouble();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in instant query: " + e.getMessage());
        }
        return null;
    }

    private List<TimeSeriesDataDTO> parsePrometheusResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.path("data").path("result");
            
            List<TimeSeriesDataDTO> timeSeriesData = new ArrayList<>();
            
            for (JsonNode series : result) {
                String metricName = extractMetricName(series.path("metric"));
                JsonNode values = series.path("values");
                
                List<TimeSeriesDataDTO.DataPoint> dataPoints = new ArrayList<>();
                for (JsonNode value : values) {
                    long timestamp = value.get(0).asLong() * 1000; // Convert to milliseconds
                    double val = Double.parseDouble(value.get(1).asText());
                    dataPoints.add(new TimeSeriesDataDTO.DataPoint(timestamp, val));
                }
                
                timeSeriesData.add(new TimeSeriesDataDTO(metricName, dataPoints));
            }
            
            return timeSeriesData;
            
        } catch (Exception e) {
            System.err.println("Error parsing Prometheus response: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private String extractMetricName(JsonNode metric) {
        // Extract a meaningful name from metric labels
        String job = metric.path("job").asText("");
        String instance = metric.path("instance").asText("");
        String service = metric.path("service").asText("");
        
        if (!service.isEmpty()) return service;
        if (!job.isEmpty()) return job;
        if (!instance.isEmpty()) return instance;
        return "unknown";
    }
}