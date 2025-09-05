package org.marionette.controlplane.usecases.inbound.abntest;

import java.time.Duration;
import java.util.List;

import org.marionette.controlplane.domain.entities.abntest.AbnTestResultsStorage;
import org.marionette.controlplane.domain.entities.abntest.SingleAbnTestResult;
import org.marionette.controlplane.usecases.inbound.AbnTestAllSystemConfigurationsUseCase;
import org.marionette.controlplane.usecases.inbound.AbnTestResult;
import org.marionette.controlplane.usecases.inbound.abntest.domain.GlobalMetricsRegistry;
import org.marionette.controlplane.usecases.inbound.abntest.domain.SystemBehaviourConfiguration;
import org.marionette.controlplane.usecases.inbound.abntest.domain.VariationPoint;
import org.marionette.controlplane.usecases.inbound.abntest.engine.AbnTestExecutor;
import org.marionette.controlplane.usecases.inbound.abntest.engine.SystemConfigurationsGenerator;
import org.marionette.controlplane.usecases.inbound.abntest.engine.VariationPointsExtractor;
import org.marionette.controlplane.usecases.inbound.abntest.ranking.SimpleConfigurationRanking;
import org.marionette.controlplane.usecases.inbound.abntest.ranking.SystemConfigurationsRanker;

public class AbnTestAllSystemConfigurationsUseCaseImpl implements AbnTestAllSystemConfigurationsUseCase {

    private final VariationPointsExtractor variationPointsExtractor;
    private final SystemConfigurationsGenerator systemConfigurationsGenerator;
    private final AbnTestExecutor executor;
    private final SystemConfigurationsRanker ranker;
    private final AbnTestResultsStorage resultsStorage;

    public AbnTestAllSystemConfigurationsUseCaseImpl(
        VariationPointsExtractor variationPointsExtractor, 
        SystemConfigurationsGenerator systemConfigurationsGenerator, 
        AbnTestExecutor executor,
        SystemConfigurationsRanker ranker,
        AbnTestResultsStorage resultsStorage) {
        this.variationPointsExtractor = variationPointsExtractor;
        this.systemConfigurationsGenerator = systemConfigurationsGenerator;
        this.executor = executor;
        this.ranker = ranker;
        this.resultsStorage = resultsStorage;
    }

    @Override
    public AbnTestResult execute() {
        
        List<VariationPoint> variationPoints = variationPointsExtractor.extractAllVariationPoints();
        
        List<SystemBehaviourConfiguration> systemConfigs =  systemConfigurationsGenerator.generateAllSystemConfigurations(variationPoints);
        
        GlobalMetricsRegistry globalMetricsRegistry = executor.executeAbnTest(systemConfigs, Duration.ofSeconds(120));
        
        List<SimpleConfigurationRanking> systemConfigRanking = ranker.rankConfigurations(globalMetricsRegistry.getAllMetrics());

        resultsStorage.putResults(
            new SingleAbnTestResult(
                globalMetricsRegistry,
                systemConfigRanking
            )
        );

        return new AbnTestResult();
    }
    
}
