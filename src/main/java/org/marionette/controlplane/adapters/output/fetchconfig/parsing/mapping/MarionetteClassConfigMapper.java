package org.marionette.controlplane.adapters.output.fetchconfig.parsing.mapping;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteMethodConfigDTO;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.MethodConfigData;

public class MarionetteClassConfigMapper {

    public static ClassConfigData toDomainClassConfigData(MarionetteClassConfigDTO classConfigDTO) {

        String className = classConfigDTO.getName();

        List<MethodConfigData> methodConfigs = new ArrayList<>();
        for(MarionetteMethodConfigDTO methodConfigDTO : classConfigDTO.getMethods()) {
           methodConfigs.add(new MethodConfigData(
                methodConfigDTO.getName(),
                methodConfigDTO.getCurrentBehaviourId(),
                methodConfigDTO.getAvailableBehaviourIds()
            ));
        }

        return new ClassConfigData(className, methodConfigs);
        
    }

    
}
