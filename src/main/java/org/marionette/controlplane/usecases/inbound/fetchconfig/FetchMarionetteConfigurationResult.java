package org.marionette.controlplane.usecases.inbound.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record FetchMarionetteConfigurationResult (ServiceConfigData serviceData) {}
