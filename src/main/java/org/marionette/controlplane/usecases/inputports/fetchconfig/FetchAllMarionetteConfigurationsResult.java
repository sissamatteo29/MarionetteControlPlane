package org.marionette.controlplane.usecases.inputports.fetchconfig;

import java.util.List;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record FetchAllMarionetteConfigurationsResult (List<ServiceConfigData> serviceConfigs) {}
