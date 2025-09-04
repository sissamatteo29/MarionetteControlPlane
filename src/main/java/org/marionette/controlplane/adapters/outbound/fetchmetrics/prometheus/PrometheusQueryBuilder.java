package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.domain.PrometheusMetricConfig;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.ServiceAggregator;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.TimeAggregator;

public class PrometheusQueryBuilder {

    public static String buildAggregationQuery(
            String prometheusUrl, String internalPath, String serviceName, PrometheusMetricConfig metricConfig,
            Duration timeSpan, Duration samplingPeriod) {

        // Build the PromQL query first
        StringBuilder promqlQuery = new StringBuilder();

        promqlQuery.append(metricConfig.getServiceAggregator())
                .append(" by (service) ")
                .append("(");

        switch (metricConfig.getTimeAggregator()) {
            case MAX, MIN, SUM, AVERAGE:
                promqlQuery.append(metricConfig.getTimeAggregator().toString().toLowerCase())
                        .append("_over_time");
                break;
            case RATE, INCREASE:
                promqlQuery.append(metricConfig.getTimeAggregator().toString().toLowerCase());
                break;
        }

        promqlQuery.append("(")
                .append(metricConfig.getQuery())
                .append(String.format("{service=\"%s\"}", serviceName))
                .append(String.format("[%s:%s]", toPrometheus(timeSpan), toPrometheus(samplingPeriod)))
                .append("))");

        // Now URL-encode the entire query
        String encodedQuery = URLEncoder.encode(promqlQuery.toString(), StandardCharsets.UTF_8);

        // Build the final URL
        String finalUrl =  prometheusUrl + internalPath + "?query=" + encodedQuery;

        System.out.println("Built a new url " + finalUrl);

        return finalUrl;
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
        PrometheusMetricConfig config = new PrometheusMetricConfig();
        config.setQuery("http_server_requests_seconds_count");
        config.setTimeAggregator(TimeAggregator.AVERAGE);
        config.setServiceAggregator(ServiceAggregator.SUM);

        // Run builder with 5 minutes
        String query = PrometheusQueryBuilder.buildAggregationQuery(
                "http://localhost:9090",
                "/api/v1/query",
                "image-processor-service",
                config,
                Duration.ofMinutes(5),
                Duration.ofSeconds(13));

        System.out.println("Generated query:\n" + query);

        PrometheusMetricConfig config2 = new PrometheusMetricConfig();
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
                    span,
                    Duration.ofSeconds(13));

            System.out.printf("Span: %-8s -> %s%n", span, query2);
        }
    }

}
