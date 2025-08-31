package org.marionette.controlplane.usecases.inbound.fetchconfig;

import java.util.List;

public record FetchAllMarionetteConfigurationsRequest (List<String> serviceEndpoints) {} 
