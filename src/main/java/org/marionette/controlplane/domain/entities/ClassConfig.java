package org.marionette.controlplane.domain.entities;

import java.util.Map;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.MethodName;

/**
 * Does not contain duplicate elements following the convention on equality expressed by MethodConfig equals() method 
 * @param methodName
 */
public class ClassConfig {

    private final Map<MethodName, MethodConfig> methodsConfig = new HashMap<>();  // Aggregate relationship between class config and method config

    public void addMethodConfig(MethodName methodName, MethodConfig methodConfig) {
        requireNonNull(methodConfig, "Trying to add a null MethodConfig object inside a ClassConfig");
        requireNonNull(methodName, "Trying to add a method configuration with a null name to the ClassConfig object");
        methodsConfig.put(methodName, methodConfig);
    }

    public void addAll(Map<MethodName, MethodConfig> configurations) {
        requireNonNull(configurations, "Trying to add configurations to a ClassConfig object with a null map");
        methodsConfig.putAll(configurations);
    }

    public void removeMethodConfig(MethodName methodName) {
        requireNonNull(methodName, "Trying to remove a MethodConfig object inside a ClassConfig with a null MethodName reference");
        methodsConfig.remove(methodName);
    }

    public void modifyCurrentBehaviour(MethodName method, BehaviourId newBehaviour) {
        requireNonNull(method, "The method name cannot be null");
        requireNonNull(newBehaviour, "The newBehaviour cannot be null");

        if(!methodsConfig.containsKey(method)) {
            throw new IllegalArgumentException("The method with name " + method.getMethodName() + " does not exist in the current class configuration");
        }

        methodsConfig.get(method).setCurrentBehaviourId(newBehaviour); // LoD
    }

    
}
