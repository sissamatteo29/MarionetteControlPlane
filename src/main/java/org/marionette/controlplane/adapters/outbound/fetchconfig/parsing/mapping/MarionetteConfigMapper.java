package org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.mapping;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.dto.MarionetteServiceConfigDTO;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public class MarionetteConfigMapper {

    public static ServiceConfigData toDomainServiceConfigData(MarionetteServiceConfigDTO marionetteServiceConfigDTO) {
        List<ClassConfigData> classConfigs = new ArrayList<>();
        for(MarionetteClassConfigDTO classConfigDTO : marionetteServiceConfigDTO.getClasses()) {
            classConfigs.add(
                MarionetteClassConfigMapper.toDomainClassConfigData(classConfigDTO)   
            );
        }
        return new ServiceConfigData(marionetteServiceConfigDTO.getServiceName(), classConfigs);
    }
    
}
