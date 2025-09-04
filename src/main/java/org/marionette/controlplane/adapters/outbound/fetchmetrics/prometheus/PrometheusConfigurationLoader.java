package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.PrometheusMetricConfig;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.OptimizationDirection;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.ServiceAggregator;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.TimeAggregator;


public class PrometheusConfigurationLoader {

    public static PrometheusConfiguration loadFromEnv() {
        
        System.out.println("== INITIATING LOAD OF PROMETHEUS CONFIGS ==");

        String prometheusUrl = "";

        List<String> envVarNames = Arrays.asList(
                "PROMETHEUS_URL", // Primary
                "PROMETHEUS_ENDPOINT", // Alternative
                "PROMETHEUS_SERVICE_URL", // Kubernetes style
                "MONITORING_PROMETHEUS_URL" // Namespace-specific
        );

        for (String envVar : envVarNames) {
            String url = System.getenv(envVar);
            if (url != null && !url.trim().isEmpty()) {
                System.out.println("Found Prometheus URL in environment variable: " + url);
                prometheusUrl = url.trim();
            }
        }

        if(prometheusUrl.equals("")) {
            System.out.println("The prometheus url was not found through environment variables");
        }

        // Pattern to match: MARIONETTE_METRICS_QUERIES_<KEY>_<PROPERTY>
        Pattern pattern = Pattern.compile(
                "^MARIONETTE_METRICS_CONFIG_([A-Z_]+)_(QUERY|TIMEAGGREGATOR|SERVICEAGGREGATOR|ORDER|DIRECTION|DISPLAYNAME|UNIT|DESCRIPTION)$");

        Map<String, PrometheusMetricConfig> metrics = new LinkedHashMap<>();    // Maintains order of insertion!

        // Iterate through all environment variables
        System.getenv().forEach((key, value) -> {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                String queryKey = matcher.group(1).toLowerCase();
                String property = matcher.group(2).toLowerCase();

                // Get or create MetricQueryConfig for this queryKey
                PrometheusMetricConfig config = metrics.computeIfAbsent(queryKey, k -> new PrometheusMetricConfig());

                // Set the appropriate property
                switch (property) {
                    case "query":
                        config.setQuery(value);
                        break;
                    case "timeaggregator":
                        config.setTimeAggregator(TimeAggregator.fromString(value));
                        break;
                    case "serviceaggregator":
                        config.setServiceAggregator(ServiceAggregator.fromString(value));
                        break;                    
                    case "order":
                        config.setOrder(Integer.parseInt(value));
                        break;
                    case "direction":
                        config.setDirection(OptimizationDirection.fromString(value));
                        break;
                    case "displayname":
                        config.setDisplayName(value);
                        break;
                    case "unit":
                        config.setUnit(value);
                        break;
                    case "description":
                        config.setDescription(value);
                        break;
                }
            }
        });

        List<PrometheusMetricConfig> resultingConfigs = metrics.entrySet().stream()
            .sorted((a, b) -> {
                int priorityA = a.getValue().getOrder();
                int priorityB = b.getValue().getOrder();
                return Integer.compare(priorityA, priorityB);
            })
            .map(entry -> entry.getValue())
            .collect(Collectors.toList());

        System.out.println("== FINISHED LOAD OF PROMETHEUS CONFIGS ==");
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(
            prometheusUrl,
            resultingConfigs
        );

        System.out.println(prometheusConfiguration);

        return prometheusConfiguration;

    }

}
