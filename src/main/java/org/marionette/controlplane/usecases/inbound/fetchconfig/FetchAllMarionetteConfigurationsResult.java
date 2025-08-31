package org.marionette.controlplane.usecases.inbound.fetchconfig;

import java.util.List;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record FetchAllMarionetteConfigurationsResult (List<ServiceConfigData> serviceConfigs) {}
