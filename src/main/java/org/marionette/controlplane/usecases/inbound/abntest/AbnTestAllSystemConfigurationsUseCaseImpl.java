package org.marionette.controlplane.usecases.inbound.abntest;

import java.util.List;

import org.marionette.controlplane.usecases.inbound.AbnTestAllSystemConfigurationsUseCase;
import org.marionette.controlplane.usecases.inbound.AbnTestResult;
import org.marionette.controlplane.usecases.inbound.abntest.domain.VariationPoint;
import org.marionette.controlplane.usecases.inbound.abntest.engine.SystemConfigurationsGenerator;
import org.marionette.controlplane.usecases.inbound.abntest.engine.VariationPointsExtractor;

public class AbnTestAllSystemConfigurationsUseCaseImpl implements AbnTestAllSystemConfigurationsUseCase {

    private final VariationPointsExtractor variationPointsExtractor;
    private final SystemConfigurationsGenerator systemConfigurationsGenerator;

    public AbnTestAllSystemConfigurationsUseCaseImpl(VariationPointsExtractor variationPointsExtractor, SystemConfigurationsGenerator systemConfigurationsGenerator) {
        this.variationPointsExtractor = variationPointsExtractor;
        this.systemConfigurationsGenerator = systemConfigurationsGenerator;
    }

    @Override
    public AbnTestResult execute() {
        List<VariationPoint> variationPoints = variationPointsExtractor.extractAllVariationPoints();
        systemConfigurationsGenerator.generateAllSystemConfigurations(variationPoints);

        return new AbnTestResult();
    }
    
}
