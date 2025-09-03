package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.OptimizationDirection;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.ServiceAggregator;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.SingleMetricConfig;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.TimeAggregator;


public class PrometheusConfigurationLoader {

    public PrometheusConfiguration loadFromEnv() {
        
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
                System.out.println("Found Prometheus URL in environment variable: " + envVar);
                prometheusUrl = envVar.trim();
            }
        }

        if(prometheusUrl.equals("")) {
            System.out.println("The prometheus url was not found through environment variables");
        }

        // Pattern to match: MARIONETTE_METRICS_QUERIES_<KEY>_<PROPERTY>
        Pattern pattern = Pattern.compile(
                "^MARIONETTE_METRICS_CONFIG_([A-Z_]+)_(QUERY|TIMEAGGREGATOR|SERVICEAGGREGATOR|DIRECTION|DISPLAYNAME|UNIT|DESCRIPTION)$");

        Map<String, SingleMetricConfig> metrics = new LinkedHashMap<>();    // Maintains order of insertion!

        // Iterate through all environment variables
        System.getenv().forEach((key, value) -> {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                String queryKey = matcher.group(1).toLowerCase();
                String property = matcher.group(2).toLowerCase();

                // Get or create MetricQueryConfig for this queryKey
                SingleMetricConfig config = metrics.computeIfAbsent(queryKey, k -> new SingleMetricConfig());

                // Set the appropriate property
                switch (property) {
                    case "query":
                        config.setQuery(value);
                        break;
                    case "timeaggregator":
                        config.setTimeAggregator(TimeAggregator.fromString(property));
                        break;
                    case "serviceaggregator":
                        config.setServiceAggregator(ServiceAggregator.fromString(property));
                        break;
                    case "direction":
                        config.setDirection(OptimizationDirection.fromString(property));
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

        List<SingleMetricConfig> resultingConfigs = new ArrayList<>(metrics.values());

        System.out.println("== FINISHED LOAD OF PROMETHEUS CONFIGS ==");
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(
            prometheusUrl,
            resultingConfigs
        );

        System.out.println(prometheusConfiguration);

        return prometheusConfiguration;

    }

}
