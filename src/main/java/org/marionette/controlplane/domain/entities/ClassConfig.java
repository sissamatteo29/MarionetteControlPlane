package org.marionette.controlplane.domain.entities;

import java.util.Map;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;

/**
 * Does not contain duplicate elements following the convention on equality expressed by MethodConfig equals() method 
 * @param methodName
 */
public class ClassConfig {

    private final ClassName className;
    private final Map<MethodName, MethodConfig> methodsConfig;

    public ClassConfig(ClassName className) {
        this.className = className;
        methodsConfig = new HashMap<>();
    }

    public ClassConfig(ClassName className, Map<MethodName, MethodConfig> methodsConfig) {
        this.className = className;

        // Defensive copy, content unmodifiable
        this.methodsConfig = new HashMap<>(methodsConfig);
    }
    
    public static ClassConfig copyOf(ClassConfig other) {
        requireNonNull(other, "Trying to copy a ClassConfig object which is null");

        ClassConfig copy = new ClassConfig(other.getClassName());
        copy.methodsConfig.putAll(other.getMethodsConfigurations());
        return copy;
    }

    public ClassConfig withAddedMethodConfig(MethodName methodName, MethodConfig methodConfig) {
        requireNonNull(methodConfig, "Trying to add a null MethodConfig object inside a ClassConfig");
        requireNonNull(methodName, "Trying to add a method configuration with a null name to the ClassConfig object");
        
        ClassConfig copy = initialiseCopy();
        copy.methodsConfig.put(methodName, methodConfig);
        return copy;
    }

    public ClassConfig withAddedAll(Map<MethodName, MethodConfig> configurations) {
        requireNonNull(configurations, "Trying to add configurations to a ClassConfig object with a null map");
        
        ClassConfig copy = initialiseCopy();
        copy.methodsConfig.putAll(configurations);
        return copy;
    }

    public ClassConfig withRemovedMethodConfig(MethodName methodName) {
        requireNonNull(methodName, "Trying to remove a MethodConfig object inside a ClassConfig with a null MethodName reference");
        ClassConfig copy = initialiseCopy();
        copy.methodsConfig.remove(methodName);
        return copy;
    }

    public ClassConfig withNewBehaviourForMethod(MethodName method, BehaviourId newBehaviour) {
        requireNonNull(method, "The method name cannot be null");
        requireNonNull(newBehaviour, "The newBehaviour cannot be null");
        if(!methodsConfig.containsKey(method)) {
            throw new IllegalArgumentException("The method with name " + method.getMethodName() + " does not exist in the current class configuration");
        }

        ClassConfig copy = initialiseCopy();
        MethodConfig newMethodConfig = copy.methodsConfig.get(method).withCurrentBehaviourId(newBehaviour);
        copy.methodsConfig.put(method, newMethodConfig);
        return copy;
    }

    public ClassName getClassName() {
        return className;
    }

    public Map<MethodName, MethodConfig> getMethodsConfigurations() {
        return Map.copyOf(methodsConfig);       // Immutable view of the map, content of the map immutable by design
    }

    private ClassConfig initialiseCopy() {
        ClassConfig copy = new ClassConfig(getClassName());
        copy.methodsConfig.putAll(getMethodsConfigurations());
        return copy;
    }



    
}
