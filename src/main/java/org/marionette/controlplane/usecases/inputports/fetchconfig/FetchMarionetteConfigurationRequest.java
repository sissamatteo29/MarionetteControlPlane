package org.marionette.controlplane.usecases.inputports.fetchconfig;

import java.net.URI;

public record FetchMarionetteConfigurationRequest (URI completeMarionetteConfigEndpoint) {}
