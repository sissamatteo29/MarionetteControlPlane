package org.marionette.controlplane.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;
import org.marionette.controlplane.domain.values.ServiceName;

import static java.util.Objects.requireNonNull;

public class ConfigRegistry {

    private final Map<ServiceName, ServiceConfig> globalServiceConfigs = new HashMap<>();

    public void addServiceConfig(ServiceName serviceName, ServiceConfig serviceConfig) {
        requireNonNull(serviceName, "Trying to a service config in the ConfigRegistry with a null service name");
        requireNonNull(serviceConfig, "The service configuration cannot be a null value");
        
        globalServiceConfigs.put(serviceName, serviceConfig); // Defensive copy
    }

    public void addAll(Map<ServiceName, ServiceConfig> configurations) {
        requireNonNull(configurations, "Trying to add configurations to the ConfigRegistry object with a null map");

        globalServiceConfigs.putAll(configurations);
    }

    public void removeServiceConfig(ServiceName serviceName) {
        requireNonNull(serviceName, "Cannot delete an element from the ConfigRegistry with a null value for the serviceName");

        if(!globalServiceConfigs.containsKey(serviceName)) {
            throw new IllegalArgumentException("The service " + serviceName + " does not exist in the ConfigRegistry");
        }

        globalServiceConfigs.remove(serviceName);
    }

    public void modifyCurrentBehaviourForMethod(ServiceName serviceName, ClassName className, MethodName methodName, BehaviourId newBehaviourId) {

        requireNonNull(serviceName, "Service name cannot be null when trying to modify the current behaviour of a method in the global configuration");
        requireNonNull(className, "Class name cannot be null when trying to modify the current behaviour of a method in the global configuration");
        requireNonNull(methodName, "Method name cannot be null when trying to modify the current behaviour of a method in the global configuration");
        requireNonNull(newBehaviourId, "The new behaviour id cannot be null when trying to modify the current behaviour of a method in the global configuration");

        if(!globalServiceConfigs.containsKey(serviceName)) {
            throw new IllegalArgumentException("The service " + serviceName + " does not exist in the ConfigRegistry");
        }
        
        ServiceConfig modifiedServiceConfig = globalServiceConfigs.get(serviceName).modifyCurrentBehaviourForMethod(className, methodName, newBehaviourId);
        globalServiceConfigs.put(serviceName, modifiedServiceConfig);
    }

}
