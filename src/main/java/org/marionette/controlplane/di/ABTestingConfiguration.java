package org.marionette.controlplane.di;

import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusConfiguration;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusConfigurationLoader;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusFetchMarionetteNodesMetricsAdapter;
import org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus.PrometheusOrderedMetricsMetadataAdapter;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.abntest.AbnTestResultsStorage;
import org.marionette.controlplane.usecases.inbound.AbnTestAllSystemConfigurationsUseCase;
import org.marionette.controlplane.usecases.inbound.abntest.AbnTestAllSystemConfigurationsUseCaseImpl;
import org.marionette.controlplane.usecases.inbound.abntest.engine.AbnTestExecutor;
import org.marionette.controlplane.usecases.inbound.abntest.engine.SystemConfigurationsGenerator;
import org.marionette.controlplane.usecases.inbound.abntest.engine.UniformAbnTestExecutor;
import org.marionette.controlplane.usecases.inbound.abntest.engine.VariationPointsExtractor;
import org.marionette.controlplane.usecases.inbound.abntest.ranking.SystemConfigurationsRanker;
import org.marionette.controlplane.usecases.inbound.abntest.ranking.SystemMetricsAggregator;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.FetchMarionetteNodesMetricsGateway;
import org.marionette.controlplane.usecases.outbound.fetchmetrics.OrderedMetricsMetadataProvider;
import org.marionette.controlplane.usecases.outbound.servicemanipulation.ControlMarionetteServiceBehaviourGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ABTestingConfiguration {

    @Bean
    public AbnTestResultsStorage testResultsStorage() {
        return new AbnTestResultsStorage();
    }
    
    @Bean
    public PrometheusConfiguration loadConfiguration() {
        return PrometheusConfigurationLoader.loadFromEnv();
    }

    @Bean
    public FetchMarionetteNodesMetricsGateway fetchMarionetteNodesMetricsAdapter(PrometheusConfiguration config) {
        return new PrometheusFetchMarionetteNodesMetricsAdapter(config);
    }

    @Bean
    public VariationPointsExtractor variationPointsExtractor(ConfigRegistry globalRegistry) {
        return new VariationPointsExtractor(globalRegistry);
    }

    @Bean
    public SystemConfigurationsGenerator systemConfigurationsGenerator() {
        return new SystemConfigurationsGenerator();
    }

    @Bean
    public AbnTestExecutor abnTestExecutor(
        ConfigRegistry globalRegistry, 
        ControlMarionetteServiceBehaviourGateway controlMarionetteGateway,
        FetchMarionetteNodesMetricsGateway fetchMarionetteMetricsGateway) {
        return new UniformAbnTestExecutor(globalRegistry, controlMarionetteGateway, fetchMarionetteMetricsGateway);
    }

    @Bean
    public OrderedMetricsMetadataProvider metricsMetadataProvider(PrometheusConfiguration config) {
        return new PrometheusOrderedMetricsMetadataAdapter(config);
    }

    @Bean
    public SystemConfigurationsRanker ranker(OrderedMetricsMetadataProvider orderedMetricsProvider, SystemMetricsAggregator systemMetricsAggregator) {
        return new SystemConfigurationsRanker(orderedMetricsProvider, systemMetricsAggregator);
    }
 
    @Bean SystemMetricsAggregator systemMetricsAggregator() {
        return new SystemMetricsAggregator();
    }

    @Bean 
    public AbnTestAllSystemConfigurationsUseCase abntestUseCase(
        VariationPointsExtractor variationPointsExtractor, 
        SystemConfigurationsGenerator systemConfigurationsGenerator, 
        AbnTestExecutor executor,
        SystemConfigurationsRanker ranker,
        AbnTestResultsStorage resultsStorage) {
        return new AbnTestAllSystemConfigurationsUseCaseImpl(
            variationPointsExtractor, 
            systemConfigurationsGenerator, 
            executor,
            ranker,
            resultsStorage);
    }
}
