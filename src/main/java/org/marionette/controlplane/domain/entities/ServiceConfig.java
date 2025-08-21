package org.marionette.controlplane.domain.entities;

import java.util.Map;
import java.util.Map.Entry;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;

public class ServiceConfig {

    private final String serviceName;
    private final Map<ClassName, ClassConfig> classesWithVariants = new HashMap<>();

    public ServiceConfig(String serviceName) {
        requireNonNull(serviceName, "The service name cannot be null");
        this.serviceName = serviceName;
    }

    public void addClassConfiguration(ClassName className, ClassConfig classConfig) {
        requireNonNull(className, "The class name cannot be null");
        requireNonNull(classConfig, "The class configuration cannot be null");

        classesWithVariants.put(className, ClassConfig.copyOf(classConfig));
    }

    public void addAll(Map<ClassName, ClassConfig> classConfigs) {
        requireNonNull(classConfigs, "Trying to add a null map to the service configuration");
        
        for(Entry<ClassName, ClassConfig> entry : classConfigs.entrySet()) {
            classesWithVariants.put(entry.getKey(), ClassConfig.copyOf(entry.getValue()));  // Defensive copy
        }

    }

    public void removeClassConfiguration(ClassName className) {
        requireNonNull(className, "The class name cannot be null");

        ensureMapContainsKey(className);

        classesWithVariants.remove(className);
    }

    public void modifyCurrentBehaviourForMethod(ClassName className, MethodName methodName,
            BehaviourId newBehaviourId) {
        requireNonNull(className, "The class name cannot be null");
        requireNonNull(methodName, "The method name cannot be null");
        requireNonNull(newBehaviourId, "The behaviour id cannot be null");

        ensureMapContainsKey(className);

        classesWithVariants.get(className).modifyCurrentBehaviour(methodName, newBehaviourId); // LoD

    }

    private void ensureMapContainsKey(ClassName className) {
        if (!classesWithVariants.containsKey(className)) {
            throw new IllegalArgumentException(
                    "The class " + className + " does not exist in the configuration of the service " + serviceName);
        }
    }

    public String toString() {
        return serviceName;
    }

}
