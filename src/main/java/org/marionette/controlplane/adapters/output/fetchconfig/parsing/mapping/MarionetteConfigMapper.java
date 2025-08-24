package org.marionette.controlplane.adapters.output.fetchconfig.parsing.mapping;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteConfigDTO;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public class MarionetteConfigMapper {

    public static ServiceConfigData toDomainServiceConfigData(MarionetteConfigDTO marionetteConfigDTO) {
        List<ClassConfigData> classConfigs = new ArrayList<>();
        for(MarionetteClassConfigDTO classConfigDTO : marionetteConfigDTO.marionetteClasses) {
            classConfigs.add(
                MarionetteClassConfigMapper.toDomainClassConfigData(classConfigDTO)   
            );
        }
        return new ServiceConfigData(marionetteConfigDTO.microserviceName, classConfigs);
    }
    
}
