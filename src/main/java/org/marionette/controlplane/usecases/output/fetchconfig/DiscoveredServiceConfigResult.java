package org.marionette.controlplane.usecases.output.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record DiscoveredServiceConfigResult (ServiceConfigData serviceConfigData) {}
