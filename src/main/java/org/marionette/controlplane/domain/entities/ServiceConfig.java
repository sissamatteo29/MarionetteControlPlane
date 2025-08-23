package org.marionette.controlplane.domain.entities;

import java.util.Map;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;

public class ServiceConfig {

    private final Map<ClassName, ClassConfig> classesWithVariants = new HashMap<>();


    public static ServiceConfig copyOf(ServiceConfig other) {
        requireNonNull(other, "The ServiceConfig other reference cannot be null when trying to copy the content");
        ServiceConfig copy = new ServiceConfig();
        copy.addAll(other.classesWithVariants);
        return copy;
    }

    public ServiceConfig addClassConfiguration(ClassName className, ClassConfig classConfig) {
        requireNonNull(className, "The class name cannot be null");
        requireNonNull(classConfig, "The class configuration cannot be null");

        ServiceConfig copy = initialiseCopy();
        copy.classesWithVariants.put(className, classConfig);
        return copy;

    }

    public ServiceConfig addAll(Map<ClassName, ClassConfig> classConfigs) {
        requireNonNull(classConfigs, "Trying to add a null map to the service configuration");
        
        ServiceConfig copy = initialiseCopy();
        copy.classesWithVariants.putAll(classConfigs);
        return copy;

    }

    public ServiceConfig removeClassConfiguration(ClassName className) {
        requireNonNull(className, "The class name cannot be null");

        ensureMapContainsKey(className);

        ServiceConfig copy = initialiseCopy();
        copy.classesWithVariants.remove(className);
        return copy;
    }

    public ServiceConfig modifyCurrentBehaviourForMethod(ClassName className, MethodName methodName,
            BehaviourId newBehaviourId) {
        requireNonNull(className, "The class name cannot be null");
        requireNonNull(methodName, "The method name cannot be null");
        requireNonNull(newBehaviourId, "The behaviour id cannot be null");

        ensureMapContainsKey(className);

        ServiceConfig copy = initialiseCopy();
        ClassConfig modifiedClassConfig = copy.classesWithVariants.get(className).modifyCurrentBehaviour(methodName, newBehaviourId);
        copy.classesWithVariants.put(className, modifiedClassConfig);
        return copy;

    }

    private void ensureMapContainsKey(ClassName className) {
        if (!classesWithVariants.containsKey(className)) {
            throw new IllegalArgumentException(
                    "The class " + className + " does not exist in the configuration of the service");
        }
    }

    private ServiceConfig initialiseCopy() {
        ServiceConfig copy = new ServiceConfig();
        copy.classesWithVariants.putAll(classesWithVariants);
        return copy;
    }

}
