package org.marionette.controlplane.usecases.inbound.abntest;

import java.time.Duration;
import java.util.List;

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

    public AbnTestAllSystemConfigurationsUseCaseImpl(
        VariationPointsExtractor variationPointsExtractor, 
        SystemConfigurationsGenerator systemConfigurationsGenerator, 
        AbnTestExecutor executor,
        SystemConfigurationsRanker ranker) {
        this.variationPointsExtractor = variationPointsExtractor;
        this.systemConfigurationsGenerator = systemConfigurationsGenerator;
        this.executor = executor;
        this.ranker = ranker;
    }

    @Override
    public AbnTestResult execute() {
        List<VariationPoint> variationPoints = variationPointsExtractor.extractAllVariationPoints();
        List<SystemBehaviourConfiguration> systemConfigs =  systemConfigurationsGenerator.generateAllSystemConfigurations(variationPoints);

        GlobalMetricsRegistry globalMetricsRegistry = executor.executeAbnTest(systemConfigs, Duration.ofSeconds(200));

        List<SimpleConfigurationRanking> systemConfigRanking = ranker.rankConfigurations(globalMetricsRegistry.getAllMetrics());

        return new AbnTestResult();
    }
    
}
