package org.marionette.controlplane.domain.entities.abntest;

import java.util.List;

import org.marionette.controlplane.usecases.inbound.abntest.domain.GlobalMetricsRegistry;
import org.marionette.controlplane.usecases.inbound.abntest.ranking.SimpleConfigurationRanking;

public record SingleAbnTestResult (
    GlobalMetricsRegistry metricsRegistry,
    List<SimpleConfigurationRanking> ranking
) {}
