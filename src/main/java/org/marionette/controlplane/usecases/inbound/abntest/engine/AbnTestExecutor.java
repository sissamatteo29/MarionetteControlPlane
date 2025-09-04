package org.marionette.controlplane.usecases.inbound.abntest.engine;

import java.time.Duration;

import org.marionette.controlplane.usecases.inbound.abntest.domain.GlobalMetricsRegistry;

public interface AbnTestExecutor {

    public GlobalMetricsRegistry executeAbnTest(Duration totalTime);
    
}
