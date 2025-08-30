package org.marionette.controlplane.adapters.output.servicediscovery;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.entities.ServiceMetadata.ServiceStatus;
import org.marionette.controlplane.domain.values.ServiceName;
import org.marionette.controlplane.usecases.domain.mappers.ServiceConfigDataMapper;
import org.marionette.controlplane.usecases.inputports.DiscoverMarionetteServicesUseCase;
import org.marionette.controlplane.usecases.inputports.servicediscovery.DiscoverMarionetteServicesResult;
import org.marionette.controlplane.usecases.outputports.fetchconfig.FetchRemoteMarionetteConfigurationResult;
import org.marionette.controlplane.usecases.outputports.fetchconfig.FetchMarionetteConfigurationGateway;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ServiceDiscoveryService {

    private final ConfigRegistry configRegistry;
    private final DiscoverMarionetteServicesUseCase serviceDiscovery;
    private final FetchMarionetteConfigurationGateway nodeConfigGateway;

    public ServiceDiscoveryService(
            ConfigRegistry configRegistry,
            DiscoverMarionetteServicesUseCase serviceDiscovery,
            FetchMarionetteConfigurationGateway nodeConfigGateway) {
        this.configRegistry = configRegistry;
        this.serviceDiscovery = serviceDiscovery;
        this.nodeConfigGateway = nodeConfigGateway;
    }

    /**
     * Full discovery - finds new services and fetches their template configurations
     * Only fetches template config for services we haven't seen before
     */
    @Async
    public CompletableFuture<DiscoveryResult> performFullDiscovery() {
        System.out.println("🔍 Starting full service discovery...");
        
        try {
            // 1. Discover all services in the cluster
            DiscoverMarionetteServicesResult discoveryResult = serviceDiscovery.findAllServices();
            List<String> allServiceEndpoints = discoveryResult.serviceNames();
            
            System.out.println("Found " + allServiceEndpoints.size() + " services in cluster");
            
            // 2. Extract service names from endpoints
            Set<ServiceName> currentServices = allServiceEndpoints.stream()
                .map(this::extractServiceNameFromEndpoint)
                .collect(Collectors.toSet());
            
            // 3. Identify new services (not in our registry)
            Set<ServiceName> existingServices = configRegistry.getAllRuntimeConfigurations().keySet();
            Set<ServiceName> newServices = currentServices.stream()
                .filter(service -> !existingServices.contains(service))
                .collect(Collectors.toSet());
            
            // 4. Mark previously known services that are no longer found as unavailable
            Set<ServiceName> missingServices = existingServices.stream()
                .filter(service -> !currentServices.contains(service))
                .collect(Collectors.toSet());
            
            missingServices.forEach(configRegistry::markServiceUnavailable);
            
            System.out.println("New services to fetch: " + newServices.size());
            System.out.println("Missing services: " + missingServices.size());
            
            // 5. Fetch template configurations only for new services
            int fetchedConfigs = 0;
            for (ServiceName serviceName : newServices) {
                String endpoint = findEndpointForService(allServiceEndpoints, serviceName);
                if (endpoint != null) {
                    if (fetchTemplateConfiguration(serviceName, endpoint)) {
                        fetchedConfigs++;
                    }
                }
            }
            
            configRegistry.updateLastDiscovery();
            
            DiscoveryResult result = new DiscoveryResult(
                currentServices.size(),
                newServices.size(), 
                fetchedConfigs,
                missingServices.size()
            );
            
            System.out.println("✅ Discovery completed: " + result);
            System.out.println("## CURRENT STATE OF REGISTRY ##");
            System.out.println(configRegistry);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            System.err.println("❌ Discovery failed: " + e.getMessage());
            return CompletableFuture.completedFuture(new DiscoveryResult(0, 0, 0, 0));
        }
    }

    /**
     * Quick discovery - just checks which services are available, doesn't fetch configs
     */
    @Async
    public CompletableFuture<DiscoveryResult> performQuickDiscovery() {
        System.out.println("⚡ Starting quick service discovery...");
        
        try {
            DiscoverMarionetteServicesResult discoveryResult = serviceDiscovery.findAllServices();
            List<String> allServiceEndpoints = discoveryResult.serviceNames();
            
            Set<ServiceName> currentServices = allServiceEndpoints.stream()
                .map(this::extractServiceNameFromEndpoint)
                .collect(Collectors.toSet());
            
            // Update service availability status
            Set<ServiceName> existingServices = configRegistry.getAllRuntimeConfigurations().keySet();
            
            existingServices.forEach(service -> {
                if (currentServices.contains(service)) {
                    // Service is still available - update metadata without changing config
                    String endpoint = findEndpointForService(allServiceEndpoints, service);
                    if (endpoint != null) {
                        var metadata = configRegistry.getAllServiceMetadata().get(service);
                        if (metadata != null) {
                            // Just update the last seen time, keep existing config
                        }
                    }
                } else {
                    configRegistry.markServiceUnavailable(service);
                }
            });
            
            configRegistry.updateLastDiscovery();
            
            int newServices = (int) currentServices.stream()
                .filter(service -> !existingServices.contains(service))
                .count();
            
            DiscoveryResult result = new DiscoveryResult(currentServices.size(), newServices, 0, 0);
            System.out.println("⚡ Quick discovery completed: " + result);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            System.err.println("❌ Quick discovery failed: " + e.getMessage());
            return CompletableFuture.completedFuture(new DiscoveryResult(0, 0, 0, 0));
        }
    }

    /**
     * Fetch template configuration for a single service
     */
    private boolean fetchTemplateConfiguration(ServiceName serviceName, String endpoint) {
        try {
            String configEndpoint = endpoint + "/api/getConfiguration";
            System.out.println("Fetching template config for " + serviceName + " from " + configEndpoint);
            
            FetchRemoteMarionetteConfigurationResult result = nodeConfigGateway.fetchConfiguration(configEndpoint);
            
            if (result.isSuccessfull()) {
                ServiceConfig templateConfig = ServiceConfigDataMapper.createServiceConfig(result.serviceConfigData());
                configRegistry.addDiscoveredService(serviceName, templateConfig, endpoint);
                return true;
            } else {
                System.err.println("Failed to fetch config for " + serviceName + ": " + result.errorMessage());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Exception fetching config for " + serviceName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract service name from Kubernetes service endpoint
     */
    private ServiceName extractServiceNameFromEndpoint(String endpoint) {
        // Extract from URLs like "http://service-name.namespace.svc.cluster.local:8080"
        try {
            String withoutProtocol = endpoint.replace("http://", "").replace("https://", "");
            String serviceName = withoutProtocol.split("\\.")[0]; // Get first part before the dot
            return new ServiceName(serviceName);
        } catch (Exception e) {
            System.err.println("Failed to extract service name from: " + endpoint);
            return new ServiceName("unknown-service");
        }
    }

    /**
     * Find endpoint for a specific service name
     */
    private String findEndpointForService(List<String> allEndpoints, ServiceName serviceName) {
        return allEndpoints.stream()
            .filter(endpoint -> extractServiceNameFromEndpoint(endpoint).equals(serviceName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Manual refresh trigger (for UI refresh button)
     */
    public CompletableFuture<DiscoveryResult> refreshServices(boolean fullRefresh) {
        if (fullRefresh) {
            return performFullDiscovery();
        } else {
            return performQuickDiscovery();
        }
    }

    /**
     * Check if discovery is needed based on time
     */
    public boolean shouldPerformDiscovery() {
        Instant lastDiscovery = configRegistry.getLastDiscovery();
        return lastDiscovery.isBefore(Instant.now().minusSeconds(300)); // 5 minutes
    }

    /**
     * Get current discovery status
     */
    public DiscoveryStatus getDiscoveryStatus() {
        return new DiscoveryStatus(
            configRegistry.getLastDiscovery(),
            configRegistry.getAllRuntimeConfigurations().size(),
            (int) configRegistry.getAllServiceMetadata().values().stream()
                .filter(metadata -> metadata.getStatus() == ServiceStatus.UNAVAILABLE)
                .count()
        );
    }

    // Result DTOs
    public static class DiscoveryResult {
        private final int totalServices;
        private final int newServices;
        private final int configsFetched;
        private final int unavailableServices;

        public DiscoveryResult(int totalServices, int newServices, int configsFetched, int unavailableServices) {
            this.totalServices = totalServices;
            this.newServices = newServices;
            this.configsFetched = configsFetched;
            this.unavailableServices = unavailableServices;
        }

        @Override
        public String toString() {
            return String.format("Total: %d, New: %d, Fetched: %d, Unavailable: %d", 
                totalServices, newServices, configsFetched, unavailableServices);
        }

        // Getters
        public int getTotalServices() { return totalServices; }
        public int getNewServices() { return newServices; }
        public int getConfigsFetched() { return configsFetched; }
        public int getUnavailableServices() { return unavailableServices; }
    }

    public static class DiscoveryStatus {
        private final Instant lastDiscovery;
        private final int totalServices;
        private final int unavailableServices;

        public DiscoveryStatus(Instant lastDiscovery, int totalServices, int unavailableServices) {
            this.lastDiscovery = lastDiscovery;
            this.totalServices = totalServices;
            this.unavailableServices = unavailableServices;
        }

        // Getters
        public Instant getLastDiscovery() { return lastDiscovery; }
        public int getTotalServices() { return totalServices; }
        public int getUnavailableServices() { return unavailableServices; }
    }
}