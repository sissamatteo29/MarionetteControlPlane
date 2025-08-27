package org.marionette.controlplane.adapters.input.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
@ConfigurationProperties(prefix = "marionette.metrics")
public class MetricsConfiguration {
    
    private Map<String, MetricQueryConfig> queries = new HashMap<>();
    private boolean enabled = true;
    private int defaultTimeRangeMinutes = 15;
    private String defaultStep = "30s";
    
    // Default configuration that works with common setups
    public MetricsConfiguration() {
        initializeDefaults();
    }
    
    private void initializeDefaults() {
        // Standard Prometheus metrics (if available)
        queries.put("response_time", new MetricQueryConfig(
            "Response Time (95th percentile)",
            "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket{service=\"{service}\"}[1m]))",
            "seconds",
            "Time"
        ));
        
        queries.put("request_rate", new MetricQueryConfig(
            "Request Rate",
            "rate(http_requests_total{service=\"{service}\"}[1m])",
            "req/s",
            "Requests per second"
        ));
        
        queries.put("error_rate", new MetricQueryConfig(
            "Error Rate",
            "rate(http_requests_total{service=\"{service}\",status=~\"4..|5..\"}[1m]) / rate(http_requests_total{service=\"{service}\"}[1m])",
            "%",
            "Error percentage"
        ));
        
        // JVM/Spring Boot Actuator metrics (more common)
        queries.put("jvm_memory", new MetricQueryConfig(
            "JVM Memory Usage",
            "jvm_memory_used_bytes{job=~\".*{service}.*\"}",
            "bytes",
            "Memory usage"
        ));
        
        queries.put("jvm_gc", new MetricQueryConfig(
            "JVM GC Time",
            "rate(jvm_gc_collection_seconds_total{job=~\".*{service}.*\"}[1m])",
            "s/s",
            "GC time per second"
        ));
        
        queries.put("cpu_usage", new MetricQueryConfig(
            "Process CPU Usage",
            "rate(process_cpu_seconds_total{job=~\".*{service}.*\"}[1m])",
            "%",
            "CPU usage"
        ));
        
        // Generic container metrics
        queries.put("container_memory", new MetricQueryConfig(
            "Container Memory",
            "container_memory_usage_bytes{container=~\".*{service}.*\"}",
            "bytes",
            "Container memory"
        ));
        
        queries.put("container_cpu", new MetricQueryConfig(
            "Container CPU",
            "rate(container_cpu_usage_seconds_total{container=~\".*{service}.*\"}[1m])",
            "cores",
            "CPU cores used"
        ));
    }
    
    // Getters and setters for Spring Boot configuration binding
    public Map<String, MetricQueryConfig> getQueries() {
        return queries;
    }
    
    public void setQueries(Map<String, MetricQueryConfig> queries) {
        this.queries = queries;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getDefaultTimeRangeMinutes() {
        return defaultTimeRangeMinutes;
    }
    
    public void setDefaultTimeRangeMinutes(int defaultTimeRangeMinutes) {
        this.defaultTimeRangeMinutes = defaultTimeRangeMinutes;
    }
    
    public String getDefaultStep() {
        return defaultStep;
    }
    
    public void setDefaultStep(String defaultStep) {
        this.defaultStep = defaultStep;
    }
    
    public static class MetricQueryConfig {
        private String displayName;
        private String query;
        private String unit;
        private String description;
        private boolean enabled = true;
        
        public MetricQueryConfig() {}
        
        public MetricQueryConfig(String displayName, String query, String unit, String description) {
            this.displayName = displayName;
            this.query = query;
            this.unit = unit;
            this.description = description;
        }
        
        // Getters and setters
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}