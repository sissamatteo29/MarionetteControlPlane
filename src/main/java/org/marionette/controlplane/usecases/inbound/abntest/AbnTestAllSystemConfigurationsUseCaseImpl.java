package org.marionette.controlplane.usecases.inbound.abntest;

import java.time.Duration;
import java.util.List;

import org.marionette.controlplane.usecases.inbound.AbnTestAllSystemConfigurationsUseCase;
import org.marionette.controlplane.usecases.inbound.AbnTestResult;
import org.marionette.controlplane.usecases.inbound.abntest.domain.SystemBehaviourConfiguration;
import org.marionette.controlplane.usecases.inbound.abntest.domain.VariationPoint;
import org.marionette.controlplane.usecases.inbound.abntest.engine.AbnTestExecutor;
import org.marionette.controlplane.usecases.inbound.abntest.engine.SystemConfigurationsGenerator;
import org.marionette.controlplane.usecases.inbound.abntest.engine.VariationPointsExtractor;

public class AbnTestAllSystemConfigurationsUseCaseImpl implements AbnTestAllSystemConfigurationsUseCase {

    private final VariationPointsExtractor variationPointsExtractor;
    private final SystemConfigurationsGenerator systemConfigurationsGenerator;
    private final AbnTestExecutor executor;

    public AbnTestAllSystemConfigurationsUseCaseImpl(VariationPointsExtractor variationPointsExtractor, SystemConfigurationsGenerator systemConfigurationsGenerator, AbnTestExecutor executor) {
        this.variationPointsExtractor = variationPointsExtractor;
        this.systemConfigurationsGenerator = systemConfigurationsGenerator;
        this.executor = executor;
    }

    @Override
    public AbnTestResult execute() {
        List<VariationPoint> variationPoints = variationPointsExtractor.extractAllVariationPoints();
        List<SystemBehaviourConfiguration> systemConfigs =  systemConfigurationsGenerator.generateAllSystemConfigurations(variationPoints);

        executor.executeAbnTest(systemConfigs, Duration.ofSeconds(720));

        return new AbnTestResult();
    }
    
}
