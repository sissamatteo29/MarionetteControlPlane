package org.marionette.controlplane.usecases.domain;

import java.util.Map;
import java.util.stream.Collectors;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.domain.mappers.ServiceConfigDataMapper;

public class ConfigRegistrySnapshot {

    private final Map<String, ServiceConfigData> snapshot;

    private ConfigRegistrySnapshot(Map<String, ServiceConfigData> snapshot) {
        this.snapshot = Map.copyOf(snapshot);
    }

    public ConfigRegistrySnapshot fromConfigRegistry(ConfigRegistry configRegistry) {
        
        Map<String, ServiceConfigData> snapshot = configRegistry
            .getAllRuntimeConfigurations()
            .entrySet()
            .stream()
            .map(entry -> Map.entry(entry.getKey().getServiceName(), ServiceConfigDataMapper.fromDomainServiceConfig(entry.getValue())))
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue()
            ));

        return new ConfigRegistrySnapshot(snapshot);

    }

    public Map<String, ServiceConfigData> getSnapshot() {
        return snapshot;
    }

    @Override
    public String toString() {
        return "ConfigRegistrySnapshot [snapshot=" + snapshot + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((snapshot == null) ? 0 : snapshot.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigRegistrySnapshot other = (ConfigRegistrySnapshot) obj;
        if (snapshot == null) {
            if (other.snapshot != null)
                return false;
        } else if (!snapshot.equals(other.snapshot))
            return false;
        return true;
    }
    
}
