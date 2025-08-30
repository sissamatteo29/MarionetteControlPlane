package org.marionette.controlplane.usecases.domain;

import java.net.URI;

public record DiscoveredMarionetteServiceMetadata (String serviceName, URI endpoint) {}