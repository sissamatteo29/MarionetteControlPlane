package org.marionette.controlplane.usecases.inbound.abntest;

import org.marionette.controlplane.usecases.inbound.AbnTestAllSystemConfigurationsUseCase;
import org.marionette.controlplane.usecases.inbound.AbnTestResult;
import org.marionette.controlplane.usecases.inbound.abntest.engine.VariationPointsExtractor;

public class AbnTestAllSystemConfigurationsUseCaseImpl implements AbnTestAllSystemConfigurationsUseCase {

    private final VariationPointsExtractor variationPointsExtractor;

    public AbnTestAllSystemConfigurationsUseCaseImpl(VariationPointsExtractor variationPointsExtractor) {
        this.variationPointsExtractor = variationPointsExtractor;
    }

    @Override
    public AbnTestResult execute() {
        variationPointsExtractor.extractAllVariationPoints();
        return new AbnTestResult();
    }
    
}
