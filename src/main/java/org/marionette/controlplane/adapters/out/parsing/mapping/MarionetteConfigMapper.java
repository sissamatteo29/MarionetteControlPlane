package org.marionette.controlplane.adapters.out.parsing.mapping;

import org.marionette.controlplane.adapters.out.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.adapters.out.parsing.dto.MarionetteConfigDTO;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.values.ClassName;

public class MarionetteConfigMapper {

    public static ServiceConfig toDomainAddServiceConfigRequest(MarionetteConfigDTO marionetteConfigDTO) {
        ServiceConfig serviceConfig = new ServiceConfig();
        for(MarionetteClassConfigDTO classConfigDTO : marionetteConfigDTO.marionetteClasses) {
            serviceConfig.addClassConfiguration(
                new ClassName(classConfigDTO.originalClass.path), 
                MarionetteClassConfigMapper.toDomain(classConfigDTO));
        }
        return serviceConfig;
    }
    
}
