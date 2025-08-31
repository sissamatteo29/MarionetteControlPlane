package org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.mapping;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.dto.MarionetteMethodConfigDTO;
import org.marionette.controlplane.adapters.outbound.fetchconfig.parsing.dto.MarionetteServiceConfigDTO;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.MethodConfigData;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public class MarionetteConfigMapper {

    public static ServiceConfigData toDomainServiceConfigData(MarionetteServiceConfigDTO marionetteServiceConfigDTO) {
        List<ClassConfigData> classConfigs = new ArrayList<>();
        for (MarionetteClassConfigDTO classConfigDTO : marionetteServiceConfigDTO.getClasses()) {
            classConfigs.add(
                    toDomainClassConfigData(classConfigDTO));
        }
        return new ServiceConfigData(marionetteServiceConfigDTO.getServiceName(), classConfigs);
    }

    private static ClassConfigData toDomainClassConfigData(MarionetteClassConfigDTO classConfigDTO) {

        String className = classConfigDTO.getName();

        List<MethodConfigData> methodConfigs = new ArrayList<>();
        for(MarionetteMethodConfigDTO methodConfigDTO : classConfigDTO.getMethods()) {
           methodConfigs.add(new MethodConfigData(
                methodConfigDTO.getName(),
                methodConfigDTO.getCurrentBehaviourId(),
                methodConfigDTO.getCurrentBehaviourId(),        // TODO: expose data from Marionette service
                methodConfigDTO.getAvailableBehaviours()
            ));
        }

        return new ClassConfigData(className, methodConfigs);
        
    }

}
