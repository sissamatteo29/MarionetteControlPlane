package org.marionette.controlplane.adapters.input.controllers;

import org.marionette.controlplane.adapters.input.metrics.PrometheusClient;
import org.marionette.controlplane.adapters.input.metrics.TimeSeriesDataDTO;
import org.marionette.controlplane.adapters.input.metrics.MetricsDTO;
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
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(minutes, ChronoUnit.MINUTES);
            
            // Query key metrics from Prometheus
            List<TimeSeriesDataDTO> responseTime = prometheusClient.queryRangeHistogramPercentile(
                "http_request_duration_seconds", 
                "0.95", 
                serviceName, 
                startTime, 
                endTime,
                "30s"
            );
            
            List<TimeSeriesDataDTO> requestRate = prometheusClient.queryRangeRate(
                "http_requests_total", 
                serviceName, 
                startTime, 
                endTime,
                "30s"
            );
            
            List<TimeSeriesDataDTO> errorRate = prometheusClient.queryRangeErrorRate(
                "http_requests_total", 
                serviceName, 
                startTime, 
                endTime,
                "30s"
            );
            
            List<TimeSeriesDataDTO> cpuUsage = prometheusClient.queryRange(
                "rate(container_cpu_usage_seconds_total{pod=~\"" + serviceName + ".*\"}[1m])", 
                startTime, 
                endTime,
                "30s"
            );
            
            List<TimeSeriesDataDTO> memoryUsage = prometheusClient.queryRange(
                "container_memory_usage_bytes{pod=~\"" + serviceName + ".*\"}", 
                startTime, 
                endTime,
                "30s"
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

            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
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
            Map<String, Double> liveMetrics = prometheusClient.getCurrentMetrics(serviceName);
            return ResponseEntity.ok(liveMetrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/metrics/{serviceName}/method/{methodName} - Get method-specific metrics
     * If your instrumentation includes method-level metrics
     */
    @GetMapping("/{serviceName}/method/{methodName}")
    public ResponseEntity<MetricsDTO> getMethodMetrics(
            @PathVariable String serviceName,
            @PathVariable String methodName,
            @RequestParam(defaultValue = "15") int minutes) {
        
        try {
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(minutes, ChronoUnit.MINUTES);
            
            // Query method-specific metrics if available
            List<TimeSeriesDataDTO> methodResponseTime = prometheusClient.queryRangeWithMethodFilter(
                "http_request_duration_seconds", 
                serviceName, 
                methodName,
                startTime, 
                endTime,
                "30s"
            );
            
            List<TimeSeriesDataDTO> methodRequestRate = prometheusClient.queryRangeRateWithMethodFilter(
                "http_requests_total", 
                serviceName, 
                methodName,
                startTime, 
                endTime,
                "30s"
            );

            MetricsDTO metrics = new MetricsDTO(
                serviceName + "." + methodName,
                methodResponseTime,
                methodRequestRate,
                null, // error rate
                null, // cpu usage
                null, // memory usage
                startTime,
                endTime
            );

            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}