package org.marionette.controlplane.domain.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

import org.marionette.controlplane.domain.values.ClassName;

/**
 * Does not contain duplicate elements following the convention on equality expressed by MethodConfig equals() method 
 * @param methodName
 */
public class ClassConfig {

    private final ClassName className;
    private final Set<MethodConfig> methodsConfig = new HashSet<>();  // Aggregate relationship
    
    public ClassConfig(ClassName className, List<MethodConfig> methodsConfig) {

        this.className = requireNonNull(className, "Trying to build a ClassConfig object with null className");
        this.methodsConfig.addAll(requireNonNull(methodsConfig, "Trying to build a ClassConfig object with a null reference for the method configurations set"));
    
    }

    public void addMethodConfig(MethodConfig methodConfig) {
        requireNonNull(methodConfig, "Trying to add a null MethodConfig object inside a ClassConfig");
        methodsConfig.add(methodConfig);
    }

    public void removeMethodConfig(MethodConfig methodConfig) {
        requireNonNull(methodConfig, "Trying to remove a null MethodConfig object inside a ClassConfig");
        methodsConfig.remove(methodConfig);
    }

    public ClassName getClassName() {
        return className;
    }





    
}
