package org.marionette.controlplane.usecases.inbound.storeconfig;

import java.net.URI;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record StoreMarionetteServiceConfigurationRequest (ServiceConfigData serviceConfigData, URI serviceEndpoint) {}
