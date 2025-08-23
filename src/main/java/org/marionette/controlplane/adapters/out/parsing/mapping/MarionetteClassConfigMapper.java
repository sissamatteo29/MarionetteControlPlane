package org.marionette.controlplane.adapters.out.parsing.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.marionette.controlplane.adapters.out.parsing.dto.BehaviourConfigDTO;
import org.marionette.controlplane.adapters.out.parsing.dto.GenericClassConfigDTO;
import org.marionette.controlplane.adapters.out.parsing.dto.MarionetteClassConfigDTO;
import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.values.MethodName;

public class MarionetteClassConfigMapper {

    public static ClassConfig toDomain(MarionetteClassConfigDTO classConfigDTO) {

        Map<String, List<String>> behavioursByMethod = new HashMap<>();
        behavioursByMethod.putAll(extractBehavioursByMethod(classConfigDTO.originalClass));
        for(GenericClassConfigDTO variantsConfigs : classConfigDTO.variantClasses) {
            behavioursByMethod.putAll(extractBehavioursByMethod(variantsConfigs));
        }

        return generateDomainClassConfig(behavioursByMethod);
        
    }


    private static Map<String, List<String>> extractBehavioursByMethod(GenericClassConfigDTO classConfigDTO) {
        Map<String, List<String>> behavioursByMethod = new HashMap<>();
        for(BehaviourConfigDTO behaviourConfigDTO : classConfigDTO.behaviours) {
            behavioursByMethod.computeIfAbsent(behaviourConfigDTO.behaviourName, s -> new ArrayList<>()).add(behaviourConfigDTO.behaviourId);
        }
        return behavioursByMethod;
    }

    
    private static ClassConfig generateDomainClassConfig(Map<String, List<String>> behavioursByMethod) {
        ClassConfig classConfig = new ClassConfig();

        for(Entry<String, List<String>> entry : behavioursByMethod.entrySet()) {

            MethodName methodName = new MethodName(entry.getKey());
            String defaultBehaviourId = entry.getValue().get(0);
            String currentBehaviourId = defaultBehaviourId;
            MethodConfig methodConfig = MethodConfig.of(defaultBehaviourId, currentBehaviourId, entry.getValue());

            classConfig.addMethodConfig(methodName, methodConfig);
            
        }

        return classConfig;

    }



    
}
