package org.marionette.controlplane.adapters.inbound.dto;

import java.util.List;

public record AllServiceConfigsDTO (List<ServiceConfigDTO> serviceConfigs) {

    public AllServiceConfigsDTO {
        serviceConfigs = List.copyOf(serviceConfigs);
    }
    
}
