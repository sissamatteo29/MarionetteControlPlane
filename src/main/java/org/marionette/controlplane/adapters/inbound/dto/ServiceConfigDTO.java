package org.marionette.controlplane.adapters.inbound.dto;

import java.util.List;

public record ServiceConfigDTO (String serviceName, List<ClassConfigDTO> classConfigs) {}
