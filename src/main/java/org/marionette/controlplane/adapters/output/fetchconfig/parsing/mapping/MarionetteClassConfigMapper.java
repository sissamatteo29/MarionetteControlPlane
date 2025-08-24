package org.marionette.controlplane.adapters.output.fetchconfig.parsing.mapping;

import java.util.ArrayList;
import java.util.List;

import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.BehaviourConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.GenericClassConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.MethodConfigData;

public class MarionetteClassConfigMapper {

    public static ClassConfigData toDomainClassConfigData(MarionetteClassConfigDTO classConfigDTO) {

        String className = classConfigDTO.originalClass.path;

        List<MethodConfigData> methodConfigs = new ArrayList<>();
        // originalclass
        for(BehaviourConfigDTO originalBehaviour : classConfigDTO.originalClass.behaviours) {

            List<String> behaviourIds = new ArrayList<>();
            behaviourIds.add(originalBehaviour.behaviourId);
            String methodName = originalBehaviour.behaviourName;
            String originalBehaviourId = originalBehaviour.behaviourId;

            for(GenericClassConfigDTO variantClass : classConfigDTO.variantClasses) {
                for(BehaviourConfigDTO variantBehaviour : variantClass.behaviours) {
                    if(variantBehaviour.behaviourName.equals(originalBehaviour.behaviourName)) {
                        behaviourIds.add(variantBehaviour.behaviourId);
                    }
                }
            }

            methodConfigs.add(
                new MethodConfigData(methodName, originalBehaviourId, behaviourIds)
            );

        }

        return new ClassConfigData(className, methodConfigs);
        
    }

    
}
