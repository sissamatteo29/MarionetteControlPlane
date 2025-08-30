package org.marionette.controlplane.usecases.inputports.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record FetchMarionetteConfigurationResult (ServiceConfigData serviceData) {}
