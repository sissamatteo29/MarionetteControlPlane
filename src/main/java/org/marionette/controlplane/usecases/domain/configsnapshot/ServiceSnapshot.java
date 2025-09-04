package org.marionette.controlplane.usecases.domain.configsnapshot;

import java.util.Map;
import java.util.stream.Collectors;

import org.marionette.controlplane.domain.entities.ServiceConfig;

public record ServiceSnapshot(
    String serviceName,
    Map<String, ClassSnapshot> classes
) {
    public ServiceSnapshot {
        classes = Map.copyOf(classes);
    }
    
    public static ServiceSnapshot fromServiceConfig(ServiceConfig serviceConfig) {
        Map<String, ClassSnapshot> classes = serviceConfig.getClassConfigurations()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().getClassName(),
                entry -> ClassSnapshot.fromClassConfig(entry.getValue())
            ));
            
        return new ServiceSnapshot(serviceConfig.serviceNameAsString(), classes);
    }
}