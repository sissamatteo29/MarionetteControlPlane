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

    public static ClassConfig copyOf(ClassConfig other) {
        requireNonNull(other, "Trying to copy a ClassConfig object which is null");

        ClassConfig copy = new ClassConfig();
        copy.methodsConfig.putAll(other.methodsConfig);
        return copy;
    }

    public ClassConfig addMethodConfig(MethodName methodName, MethodConfig methodConfig) {
        requireNonNull(methodConfig, "Trying to add a null MethodConfig object inside a ClassConfig");
        requireNonNull(methodName, "Trying to add a method configuration with a null name to the ClassConfig object");
        
        ClassConfig copy = new ClassConfig();
        copy.methodsConfig.putAll(methodsConfig);
        copy.methodsConfig.put(methodName, methodConfig);
        return copy;
    }

    public ClassConfig addAll(Map<MethodName, MethodConfig> configurations) {
        requireNonNull(configurations, "Trying to add configurations to a ClassConfig object with a null map");
        
        ClassConfig copy = new ClassConfig();
        copy.methodsConfig.putAll(methodsConfig);
        copy.methodsConfig.putAll(configurations);
        return copy;
    }

    public ClassConfig removeMethodConfig(MethodName methodName) {
        requireNonNull(methodName, "Trying to remove a MethodConfig object inside a ClassConfig with a null MethodName reference");
        ClassConfig copy = new ClassConfig();
        copy.methodsConfig.putAll(methodsConfig);
        copy.methodsConfig.remove(methodName);
        return copy;
    }

    public ClassConfig modifyCurrentBehaviour(MethodName method, BehaviourId newBehaviour) {
        requireNonNull(method, "The method name cannot be null");
        requireNonNull(newBehaviour, "The newBehaviour cannot be null");

        if(!methodsConfig.containsKey(method)) {
            throw new IllegalArgumentException("The method with name " + method.getMethodName() + " does not exist in the current class configuration");
        }

        ClassConfig copy = new ClassConfig();
        copy.methodsConfig.putAll(methodsConfig);
        MethodConfig newMethodConfig = copy.methodsConfig.get(method).changeCurrentBehaviourId(newBehaviour);
        copy.methodsConfig.put(method, newMethodConfig);
        return copy;
    }

    
}
