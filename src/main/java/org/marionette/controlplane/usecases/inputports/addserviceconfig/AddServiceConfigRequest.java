package org.marionette.controlplane.usecases.inputports.addserviceconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public record AddServiceConfigRequest (ServiceConfigData serviceConfigData) {}
