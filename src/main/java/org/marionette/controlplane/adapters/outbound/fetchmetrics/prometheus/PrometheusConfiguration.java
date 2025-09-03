package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.util.List;

public class PrometheusConfiguration {

    private final String url;
    private final String apiPath = "/api/v1";
    private final List<SingleMetricConfig> metrics;
    
    public PrometheusConfiguration(String url, List<SingleMetricConfig> metrics) {
        this.url = url;
        this.metrics = metrics;
    }

    public String getUrl() {
        return url;
    }

    public String getApiPath() {
        return apiPath;
    }

    public List<SingleMetricConfig> getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        if (metrics == null || metrics.isEmpty()) {
            return String.format("PrometheusConfiguration{\n" +
                "  url: %s\n" +
                "  apiPath: %s\n" +
                "  metrics: empty\n" +
                "}", url, apiPath);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("PrometheusConfiguration{\n");
        sb.append(String.format("  url: %s\n", url));
        sb.append(String.format("  apiPath: %s\n", apiPath));
        sb.append(String.format("  fullEndpoint: %s%s\n", url, apiPath));
        sb.append(String.format("  metrics: (%d configured) [\n", metrics.size()));

        for (int i = 0; i < metrics.size(); i++) {
            SingleMetricConfig metric = metrics.get(i);
            sb.append(String.format("    [%d] %s", i + 1, metric.toString()));
            if (i < metrics.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    
    
    

    
}
