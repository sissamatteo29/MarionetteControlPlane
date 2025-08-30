package org.marionette.controlplane.usecases.inputports.fetchconfig;

import java.net.URI;
import java.util.List;

public record FetchAllMarionetteConfigurationsRequest (List<URI> completeMarionetteConfigEndpoints) {} 
