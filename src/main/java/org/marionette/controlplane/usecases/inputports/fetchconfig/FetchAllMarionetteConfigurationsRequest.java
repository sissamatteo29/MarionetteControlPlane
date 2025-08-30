package org.marionette.controlplane.usecases.inputports.fetchconfig;

import java.util.List;

public record FetchAllMarionetteConfigurationsRequest (List<String> serviceEndpoints) {} 
