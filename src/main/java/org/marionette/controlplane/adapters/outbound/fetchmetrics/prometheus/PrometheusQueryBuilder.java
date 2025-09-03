package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.time.Duration;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.ServiceAggregator;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.SingleMetricConfig;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.TimeAggregator;

public class PrometheusQueryBuilder {

    public static String buildAggregationQuery(
            String prometheusUrl, String internalPath, String serviceName, SingleMetricConfig metricConfig,
            Duration timeSpan) {

        StringBuilder buildQueryUrl = new StringBuilder(prometheusUrl)
                .append(internalPath)
                .append("?query=")
                .append(metricConfig.getServiceAggregator())
                .append(" by (service) ")
                .append("(");

        switch (metricConfig.getTimeAggregator()) {
            case MAX, MIN, SUM, AVERAGE:
                buildQueryUrl.append(metricConfig.getTimeAggregator() + "_over_time");
                break;
            case RATE, INCREASE:
                buildQueryUrl.append(metricConfig.getTimeAggregator());
                break;

        }

        buildQueryUrl.append("(")
                .append(metricConfig.getQuery());

        buildQueryUrl.append(String.format("{service=\"%s\"}", serviceName));

        buildQueryUrl.append(String.format("[%s:15s]", toPrometheus(timeSpan)));

        buildQueryUrl.append("))");

        return buildQueryUrl.toString();

    }

    public static String toPrometheus(Duration duration) {
        long seconds = duration.getSeconds();

        if (seconds % 31536000 == 0) { // years (365d)
            return (seconds / 31536000) + "y";
        } else if (seconds % 604800 == 0) { // weeks
            return (seconds / 604800) + "w";
        } else if (seconds % 86400 == 0) { // days
            return (seconds / 86400) + "d";
        } else if (seconds % 3600 == 0) { // hours
            return (seconds / 3600) + "h";
        } else if (seconds % 60 == 0) { // minutes
            return (seconds / 60) + "m";
        } else {
            return seconds + "s"; // fallback
        }
    }

    public static void main(String[] args) {
        // Build a sample metric config
        SingleMetricConfig config = new SingleMetricConfig();
        config.setQuery("http_server_requests_seconds_count");
        config.setTimeAggregator(TimeAggregator.AVERAGE);
        config.setServiceAggregator(ServiceAggregator.SUM);

        // Run builder with 5 minutes
        String query = PrometheusQueryBuilder.buildAggregationQuery(
                "http://localhost:9090",
                "/api/v1/query",
                "image-processor-service",
                config,
                Duration.ofMinutes(5));

        System.out.println("Generated query:\n" + query);



        SingleMetricConfig config2 = new SingleMetricConfig();
        config2.setQuery("cpu_usage_seconds_total");
        config2.setTimeAggregator(TimeAggregator.MAX);
        config2.setServiceAggregator(ServiceAggregator.AVERAGE);

        // Try a few different spans
        Duration[] spans = {
                Duration.ofSeconds(90),
                Duration.ofMinutes(5),
                Duration.ofHours(1),
                Duration.ofDays(2)
        };

        for (Duration span : spans) {
            String query2 = PrometheusQueryBuilder.buildAggregationQuery(
                    "http://localhost:9090",
                    "/api/v1/query",
                    "ui-service",
                    config,
                    span);

            System.out.printf("Span: %-8s -> %s%n", span, query);
        }
    }

}
