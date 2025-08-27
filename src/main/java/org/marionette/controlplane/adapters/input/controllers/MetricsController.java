package org.marionette.controlplane.adapters.input.controllers;

import org.marionette.controlplane.adapters.input.metrics.PrometheusClient;
import org.marionette.controlplane.adapters.input.metrics.TimeSeriesDataDTO;
import org.marionette.controlplane.adapters.input.metrics.MetricsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "*")
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);
    private final PrometheusClient prometheusClient;

    public MetricsController(PrometheusClient prometheusClient) {
        this.prometheusClient = prometheusClient;
    }

    /**
     * GET /api/metrics/{serviceName} - Get key metrics for a service
     * Returns metrics like response time, error rate, request rate, etc.
     */
    @GetMapping("/{serviceName}")
    public ResponseEntity<MetricsDTO> getServiceMetrics(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "15") int minutes) {
        
        try {
            logger.info("Fetching metrics for service: {}, time range: {} minutes", serviceName, minutes);
            
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(minutes, ChronoUnit.MINUTES);
            
            // Calculate appropriate step based on time range
            String step = calculateStep(minutes);
            logger.debug("Using step size: {} for {} minute range", step, minutes);
            
            // Query key metrics from Prometheus - with better error handling
            List<TimeSeriesDataDTO> responseTime = queryWithFallback(() -> 
                prometheusClient.queryRangeHistogramPercentile(
                    "http_request_duration_seconds", 
                    "0.95", 
                    serviceName, 
                    startTime, 
                    endTime,
                    step
                ), "responseTime", serviceName
            );
            
            List<TimeSeriesDataDTO> requestRate = queryWithFallback(() -> 
                prometheusClient.queryRangeRate(
                    "http_requests_total", 
                    serviceName, 
                    startTime, 
                    endTime,
                    step
                ), "requestRate", serviceName
            );
            
            List<TimeSeriesDataDTO> errorRate = queryWithFallback(() -> 
                prometheusClient.queryRangeErrorRate(
                    "http_requests_total", 
                    serviceName, 
                    startTime, 
                    endTime,
                    step
                ), "errorRate", serviceName
            );
            
            // Alternative metric names for CPU and memory
            List<TimeSeriesDataDTO> cpuUsage = queryWithFallback(() -> 
                prometheusClient.queryRange(
                    // Try multiple metric patterns
                    "rate(container_cpu_usage_seconds_total{container=~\"" + serviceName + ".+\"}[1m]) or " +
                    "rate(process_cpu_seconds_total{job=~\"" + serviceName + ".+\"}[1m]) or " +
                    "rate(node_cpu_seconds_total{mode!=\"idle\"}[1m])", 
                    startTime, 
                    endTime,
                    step
                ), "cpuUsage", serviceName
            );
            
            List<TimeSeriesDataDTO> memoryUsage = queryWithFallback(() -> 
                prometheusClient.queryRange(
                    // Try multiple metric patterns
                    "container_memory_usage_bytes{container=~\"" + serviceName + ".+\"} or " +
                    "process_resident_memory_bytes{job=~\"" + serviceName + ".+\"} or " +
                    "node_memory_MemTotal_bytes - node_memory_MemFree_bytes", 
                    startTime, 
                    endTime,
                    step
                ), "memoryUsage", serviceName
            );

            MetricsDTO metrics = new MetricsDTO(
                serviceName,
                responseTime,
                requestRate,
                errorRate,
                cpuUsage,
                memoryUsage,
                startTime,
                endTime
            );

            logger.info("Successfully fetched metrics for service: {}", serviceName);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Failed to fetch metrics for service: {}", serviceName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/metrics/{serviceName}/live - Get current metric values
     * For real-time updates without historical data
     */
    @GetMapping("/{serviceName}/live")
    public ResponseEntity<Map<String, Double>> getLiveMetrics(@PathVariable String serviceName) {
        try {
            logger.info("Fetching live metrics for service: {}", serviceName);
            Map<String, Double> liveMetrics = prometheusClient.getCurrentMetrics(serviceName);
            logger.info("Live metrics for {}: {}", serviceName, liveMetrics);
            return ResponseEntity.ok(liveMetrics);
        } catch (Exception e) {
            logger.error("Failed to fetch live metrics for service: {}", serviceName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/metrics/{serviceName}/method/{methodName} - Get method-specific metrics
     */
    @GetMapping("/{serviceName}/method/{methodName}")
    public ResponseEntity<MetricsDTO> getMethodMetrics(
            @PathVariable String serviceName,
            @PathVariable String methodName,
            @RequestParam(defaultValue = "15") int minutes) {
        
        try {
            logger.info("Fetching method metrics for {}.{}, time range: {} minutes", 
                       serviceName, methodName, minutes);
            
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(minutes, ChronoUnit.MINUTES);
            String step = calculateStep(minutes);
            
            List<TimeSeriesDataDTO> methodResponseTime = queryWithFallback(() -> 
                prometheusClient.queryRangeWithMethodFilter(
                    "http_request_duration_seconds", 
                    serviceName, 
                    methodName,
                    startTime, 
                    endTime,
                    step
                ), "methodResponseTime", serviceName + "." + methodName
            );
            
            List<TimeSeriesDataDTO> methodRequestRate = queryWithFallback(() -> 
                prometheusClient.queryRangeRateWithMethodFilter(
                    "http_requests_total", 
                    serviceName, 
                    methodName,
                    startTime, 
                    endTime,
                    step
                ), "methodRequestRate", serviceName + "." + methodName
            );

            MetricsDTO metrics = new MetricsDTO(
                serviceName + "." + methodName,
                methodResponseTime,
                methodRequestRate,
                null,
                null,
                null,
                startTime,
                endTime
            );

            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Failed to fetch method metrics for {}.{}", serviceName, methodName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Helper method to calculate appropriate step based on time range
     */
    private String calculateStep(int minutes) {
        if (minutes <= 5) {
            return "15s";
        } else if (minutes <= 15) {
            return "30s";
        } else if (minutes <= 60) {
            return "1m";
        } else {
            return "5m";
        }
    }

    /**
     * Helper method to query with fallback and logging
     */
    private List<TimeSeriesDataDTO> queryWithFallback(
            QueryFunction queryFunction, String metricType, String target) {
        try {
            List<TimeSeriesDataDTO> result = queryFunction.execute();
            if (result == null || result.isEmpty()) {
                logger.warn("No data returned for {} metric: {}", metricType, target);
            }
            return result;
        } catch (Exception e) {
            logger.warn("Failed to query {} metric for {}: {}", metricType, target, e.getMessage());
            return List.of(); // Return empty list instead of null
        }
    }

    /**
     * Functional interface for query execution
     */
    @FunctionalInterface
    private interface QueryFunction {
        List<TimeSeriesDataDTO> execute();
    }

    /**
     * Diagnostic endpoint to check Prometheus connectivity
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            boolean available = prometheusClient.isPrometheusAvailable();
            String config = prometheusClient.getPrometheusConfiguration();
            
            return ResponseEntity.ok(Map.of(
                "prometheusAvailable", available,
                "prometheusUrl", config,
                "timestamp", Instant.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Health check failed",
                "message", e.getMessage()
            ));
        }
    }
}