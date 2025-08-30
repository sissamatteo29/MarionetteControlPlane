package org.marionette.controlplane.usecases.outputports.servicediscovery;

import java.net.URI;

public record DiscoveredMarionetteServiceData (String serviceName, URI endpoint) {}