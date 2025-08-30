package org.marionette.controlplane.usecases.inputports.fetchconfig;

import org.marionette.controlplane.usecases.outputports.servicediscovery.DiscoveredMarionetteServiceData;

public record FetchMarionetteConfigurationRequest (DiscoveredMarionetteServiceData serviceData) {}
