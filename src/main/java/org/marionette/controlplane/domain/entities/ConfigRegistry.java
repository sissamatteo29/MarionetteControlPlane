package org.marionette.controlplane.domain.entities;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;
import org.marionette.controlplane.domain.values.ServiceName;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;


public class ConfigRegistry {
    
    // Template configurations (never change after initial load)
    private final Map<ServiceName, ServiceConfig> templateConfigurations = new ConcurrentHashMap<>();
    
    // Runtime configurations (current active state)
    private final Map<ServiceName, ServiceConfig> runtimeConfigurations = new ConcurrentHashMap<>();
    
    // Service discovery metadata
    private final Map<ServiceName, ServiceMetadata> serviceMetadata = new ConcurrentHashMap<>();
    
    // Last discovery time
    private volatile Instant lastDiscovery = Instant.now();

    /**
     * Add a newly discovered service with its template configuration
     */
    public synchronized void addDiscoveredService(ServiceName serviceName, ServiceConfig templateConfig, String endpoint) {
        // Store the immutable template
        templateConfigurations.put(serviceName, templateConfig);
        
        // If this is the first time we see this service, use template as runtime
        if (!runtimeConfigurations.containsKey(serviceName)) {
            runtimeConfigurations.put(serviceName, ServiceConfig.copyOf(templateConfig));
        }
        // If service already exists in runtime, keep existing runtime config
        
        // Update metadata
        serviceMetadata.put(serviceName, new ServiceMetadata(
            serviceName,
            endpoint,
            Instant.now(),
            ServiceStatus.DISCOVERED
        ));
        
        System.out.println("Added service: " + serviceName + " (template stored, runtime preserved)");
    }

    /**
     * Update runtime configuration (for behavior changes)
     */
    public synchronized void updateRuntimeConfiguration(ServiceName serviceName, ServiceConfig newRuntimeConfig) {
        if (!templateConfigurations.containsKey(serviceName)) {
            throw new IllegalArgumentException("Cannot update runtime config for unknown service: " + serviceName);
        }
        
        runtimeConfigurations.put(serviceName, newRuntimeConfig);
        updateServiceStatus(serviceName, ServiceStatus.MODIFIED);
    }

    /**
     * Reset service to its template configuration
     */
    public synchronized void resetToTemplate(ServiceName serviceName) {
        ServiceConfig template = templateConfigurations.get(serviceName);
        if (template != null) {
            runtimeConfigurations.put(serviceName, ServiceConfig.copyOf(template));
            updateServiceStatus(serviceName, ServiceStatus.RESET_TO_TEMPLATE);
        }
    }

    /**
     * Mark a service as unavailable (discovered before but not reachable now)
     */
    public synchronized void markServiceUnavailable(ServiceName serviceName) {
        updateServiceStatus(serviceName, ServiceStatus.UNAVAILABLE);
    }

    /**
     * Remove a service completely
     */
    public synchronized void removeService(ServiceName serviceName) {
        templateConfigurations.remove(serviceName);
        runtimeConfigurations.remove(serviceName);
        serviceMetadata.remove(serviceName);
    }


    public void modifyCurrentBehaviourForMethod(ServiceName serviceName, ClassName className, MethodName methodName,
            BehaviourId newBehaviourId) {

        requireNonNull(serviceName,
                "Service name cannot be null when trying to modify the current behaviour of a method in the global configuration");
        requireNonNull(className,
                "Class name cannot be null when trying to modify the current behaviour of a method in the global configuration");
        requireNonNull(methodName,
                "Method name cannot be null when trying to modify the current behaviour of a method in the global configuration");
        requireNonNull(newBehaviourId,
                "The new behaviour id cannot be null when trying to modify the current behaviour of a method in the global configuration");

        if (!runtimeConfigurations.containsKey(serviceName)) {
            throw new IllegalArgumentException("The service " + serviceName + " does not exist in the ConfigRegistry");
        }

        ServiceConfig modifiedServiceConfig = runtimeConfigurations.get(serviceName).withNewBehaviourForMethod(className,
                methodName, newBehaviourId);
        runtimeConfigurations.put(serviceName, modifiedServiceConfig);
    }

    /**
     * Get current runtime configuration (what the UI should show)
     */
    public ServiceConfig getRuntimeConfiguration(ServiceName serviceName) {
        return runtimeConfigurations.get(serviceName);
    }

    /**
     * Get template configuration (original from service)
     */
    public ServiceConfig getTemplateConfiguration(ServiceName serviceName) {
        return templateConfigurations.get(serviceName);
    }

    /**
     * Get all runtime configurations (for UI)
     */
    public Map<ServiceName, ServiceConfig> getAllRuntimeConfigurations() {
        return Map.copyOf(runtimeConfigurations);
    }

    /**
     * Get all services with their metadata
     */
    public Map<ServiceName, ServiceMetadata> getAllServiceMetadata() {
        return Map.copyOf(serviceMetadata);
    }

    /**
     * Check if a service has been modified from its template
     */
    public boolean isServiceModified(ServiceName serviceName) {
        ServiceConfig template = templateConfigurations.get(serviceName);
        ServiceConfig runtime = runtimeConfigurations.get(serviceName);
        
        if (template == null || runtime == null) return false;
        
        return !template.equals(runtime);
    }

    /**
     * Get services that need to be discovered/re-checked
     */
    public Set<ServiceName> getServicesNeedingDiscovery() {
        return serviceMetadata.entrySet().stream()
            .filter(entry -> entry.getValue().getStatus() == ServiceStatus.UNAVAILABLE ||
                           entry.getValue().getLastSeen().isBefore(Instant.now().minusSeconds(300))) // 5 minutes old
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toSet());
    }

    private void updateServiceStatus(ServiceName serviceName, ServiceStatus status) {
        ServiceMetadata current = serviceMetadata.get(serviceName);
        if (current != null) {
            serviceMetadata.put(serviceName, current.withStatus(status).withLastSeen(Instant.now()));
        }
    }

    public Instant getLastDiscovery() {
        return lastDiscovery;
    }

    public void updateLastDiscovery() {
        this.lastDiscovery = Instant.now();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ConfigRegistry State ===\n");
        sb.append("Last discovery: ").append(lastDiscovery).append("\n");
        sb.append("Total services: ").append(runtimeConfigurations.size()).append("\n\n");
        
        runtimeConfigurations.forEach((serviceName, config) -> {
            ServiceMetadata metadata = serviceMetadata.get(serviceName);
            boolean modified = isServiceModified(serviceName);
            
            sb.append("Service: ").append(serviceName.getServiceName()).append("\n");
            sb.append("  Status: ").append(metadata != null ? metadata.getStatus() : "UNKNOWN").append("\n");
            sb.append("  Modified: ").append(modified ? "YES" : "NO").append("\n");
            sb.append("  Classes: ").append(config.getClassConfigurations().size()).append("\n");
            if (metadata != null) {
                sb.append("  Endpoint: ").append(metadata.getEndpoint()).append("\n");
                sb.append("  Last seen: ").append(metadata.getLastSeen()).append("\n");
            }
            sb.append(" Content: " + config);
            sb.append("\n");
        });
        
        return sb.toString();
    }

    public static class ServiceMetadata {
        private final ServiceName serviceName;
        private final String endpoint;
        private final Instant lastSeen;
        private final ServiceStatus status;

        public ServiceMetadata(ServiceName serviceName, String endpoint, Instant lastSeen, ServiceStatus status) {
            this.serviceName = serviceName;
            this.endpoint = endpoint;
            this.lastSeen = lastSeen;
            this.status = status;
        }

        public ServiceMetadata withStatus(ServiceStatus newStatus) {
            return new ServiceMetadata(serviceName, endpoint, lastSeen, newStatus);
        }

        public ServiceMetadata withLastSeen(Instant newLastSeen) {
            return new ServiceMetadata(serviceName, endpoint, newLastSeen, status);
        }

        // Getters
        public ServiceName getServiceName() { return serviceName; }
        public String getEndpoint() { return endpoint; }
        public Instant getLastSeen() { return lastSeen; }
        public ServiceStatus getStatus() { return status; }
    }

    public enum ServiceStatus {
        DISCOVERED,      // Newly found
        AVAILABLE,       // Responding to health checks
        MODIFIED,        // Has runtime changes from template
        UNAVAILABLE,     // Was found before but not responding now
        RESET_TO_TEMPLATE // Recently reset to template config
    }
}