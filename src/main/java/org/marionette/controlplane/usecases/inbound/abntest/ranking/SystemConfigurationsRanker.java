package org.marionette.controlplane.usecases.inbound.abntest.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.marionette.controlplane.usecases.outbound.fetchmetrics.OrderedMetricsMetadataProvider;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.AggregateMetric;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.MetricsConfiguration;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.OrderedMetricMetadata;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.domain.SystemMetricsDataPoint;

public class SystemConfigurationsRanker {

    private final OrderedMetricsMetadataProvider metricsMetadataProvider;
    private final SystemMetricsAggregator systemMetricsAggregator;
    private final MetricsConfiguration metricsConfiguration;

    public SystemConfigurationsRanker(OrderedMetricsMetadataProvider metricsMetadataProvider,
            SystemMetricsAggregator systemMetricsAggregator) {
        this.metricsMetadataProvider = metricsMetadataProvider;
        this.systemMetricsAggregator = systemMetricsAggregator;

        System.out.println("Initialized ranker with lexicographic priority order:");
        this.metricsConfiguration = metricsMetadataProvider.loadMetrics();
        for (OrderedMetricMetadata metric : metricsConfiguration) {
            System.out.println("  " + metric.order() + ". " + metric.metricName() +
                    " (" + metric.direction() + ")");
        }
    }

    public List<SimpleConfigurationRanking> rankConfigurations(
            Map<String, SystemMetricsDataPoint> configurations) {

        if (configurations.isEmpty()) {
            return List.of();
        }

        System.out.println("Ranking " + configurations.size() + " configurations lexicographically...");

        // Step 1: Aggregate all configurations to system level
        List<ComparableSystemConfiguration> comparableConfigs = configurations.entrySet().stream()
                .map(entry -> {
                    String configId = entry.getKey();
                    SystemMetricsDataPoint systemData = entry.getValue();

                    // Aggregate to system level
                    List<AggregateMetric> aggregated = systemMetricsAggregator.aggregateByAverage(systemData);

                    return new ComparableSystemConfiguration(configId, aggregated, metricsConfiguration.getMetricsConfig());
                })
                .collect(Collectors.toList());

        // Step 2: Sort using lexicographic comparison (natural ordering)
        Collections.sort(comparableConfigs);

        // Step 3: Create ranking results
        List<SimpleConfigurationRanking> rankings = new ArrayList<>();
        for (int i = 0; i < comparableConfigs.size(); i++) {
            ComparableSystemConfiguration config = comparableConfigs.get(i);

            rankings.add(new SimpleConfigurationRanking(
                    i + 1, // rank (1-based, 1 = best)
                    config.getConfigurationId(),
                    config.getSystemMetrics()));
        }

        // Log the ranking results
        logRankingResults(rankings);

        return rankings;
    }

    private void logRankingResults(List<SimpleConfigurationRanking> rankings) {
        System.out.println("\n=== LEXICOGRAPHIC RANKING RESULTS ===");

        for (SimpleConfigurationRanking ranking : rankings) {
            System.out.println(String.format("%d. %s",
                    ranking.rank(),
                    ranking.configurationId()));

            // Show the metric values that determined this ranking
            for (OrderedMetricMetadata metricMeta : metricsConfiguration) {
                String metricName = metricMeta.metricName();
                Optional<AggregateMetric> metric = ranking.systemMetrics().stream()
                        .filter(m -> m.name().equals(metricName))
                        .findFirst();

                if (metric.isPresent()) {
                    System.out.println(String.format("   %s (%s): %.3f %s",
                            metricName,
                            metricMeta.direction(),
                            metric.get().value(),
                            metric.get().unit()));
                } else {
                    System.out.println(String.format("   %s: MISSING", metricName));
                }
            }
            System.out.println();
        }
    }

}
