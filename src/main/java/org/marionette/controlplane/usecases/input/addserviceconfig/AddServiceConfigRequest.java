package org.marionette.controlplane.usecases.input.addserviceconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record AddServiceConfigRequest (ServiceConfigData serviceConfigData) {}
