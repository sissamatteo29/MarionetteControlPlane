package org.marionette.controlplane.adapters.input.controllers;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.values.*;
import org.marionette.controlplane.adapters.input.changeconfig.ChangeConfigService;
import org.marionette.controlplane.adapters.input.dto.*;
import org.marionette.controlplane.adapters.output.servicediscovery.ServiceDiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ConfigurationController {

    private final ConfigRegistry configRegistry;
    private final String namespace;
    private final ChangeConfigService changeConfigService;
    private final ServiceDiscoveryService discoveryService;

    public ConfigurationController(
            EnhancedConfigRegistry configRegistry, 
            String namespace,
            ChangeConfigService changeConfigService,
            ServiceDiscoveryService discoveryService) {
        this.configRegistry = configRegistry;
        this.namespace = namespace;
        this.changeConfigService = changeConfigService;
        this.discoveryService = discoveryService;
    }

    /**
     * GET /api/services - Get all services with their current runtime configurations
     */
    @GetMapping("/services")
    public ResponseEntity<ServicesResponse> getAllServices(
            @RequestParam(defaultValue = "false") boolean refresh) {
        
        // Trigger discovery if requested or if it's been too long
        if (refresh || discoveryService.shouldPerformDiscovery()) {
            // Async discovery - don't block the response
            discoveryService.performQuickDiscovery();
        }

        Map<String, ServiceConfigDTO> serviceConfigs = new HashMap<>();
        Map<ServiceName, ServiceConfig> allServices = configRegistry.getAllRuntimeConfigurations();

        for (Map.Entry<ServiceName, ServiceConfig> entry : allServices.entrySet()) {
            ServiceConfig serviceConfig = entry.getValue();
            ServiceConfigDTO dto = convertToDTO(serviceConfig);
            serviceConfigs.put(entry.getKey().getServiceName(), dto);
        }

        // Add discovery metadata
        ServiceDiscoveryService.DiscoveryStatus status = discoveryService.getDiscoveryStatus();
        ServicesResponse response = new ServicesResponse(
            serviceConfigs,
            status.getLastDiscovery(),
            status.getTotalServices(),
            status.getUnavailableServices()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/services/discover - Trigger full service discovery
     */
    @PostMapping("/services/discover")
    public ResponseEntity<DiscoveryResponseDTO> triggerDiscovery(
            @RequestParam(defaultValue = "false") boolean fullRefresh) {
        
        try {
            CompletableFuture<ServiceDiscoveryService.DiscoveryResult> future = 
                discoveryService.refreshServices(fullRefresh);
            
            // For now, return immediately with status
            // In a real app, you might want to wait for completion or use webhooks
            
            return ResponseEntity.accepted().body(new DiscoveryResponseDTO(
                "Discovery started",
                fullRefresh ? "full" : "quick",
                Instant.now()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new DiscoveryResponseDTO(
                "Discovery failed: " + e.getMessage(),
                "error",
                Instant.now()
            ));
        }
    }

    /**
     * GET /api/services/{serviceName} - Get specific service configuration
     */
    @GetMapping("/services/{serviceName}")
    public ResponseEntity<ServiceDetailResponse> getService(@PathVariable String serviceName) {
        try {
            ServiceName service = new ServiceName(serviceName);
            ServiceConfig runtimeConfig = configRegistry.getRuntimeConfiguration(service);
            ServiceConfig templateConfig = configRegistry.getTemplateConfiguration(service);

            if (runtimeConfig == null) {
                return ResponseEntity.notFound().build();
            }

            ServiceConfigDTO runtimeDto = convertToDTO(runtimeConfig);
            ServiceConfigDTO templateDto = templateConfig != null ? convertToDTO(templateConfig) : null;
            
            var metadata = configRegistry.getAllServiceMetadata().get(service);
            boolean isModified = configRegistry.isServiceModified(service);

            ServiceDetailResponse response = new ServiceDetailResponse(
                runtimeDto,
                templateDto,
                isModified,
                metadata != null ? metadata.getStatus().toString() : "UNKNOWN",
                metadata != null ? metadata.getLastSeen() : null
            );

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/services/{serviceName}/reset - Reset service to template configuration
     */
    @PostMapping("/services/{serviceName}/reset")
    public ResponseEntity<String> resetServiceToTemplate(@PathVariable String serviceName) {
        try {
            ServiceName service = new ServiceName(serviceName);
            configRegistry.resetToTemplate(service);
            
            // Notify the service instances
            ServiceConfig templateConfig = configRegistry.getTemplateConfiguration(service);
            if (templateConfig != null) {
                // You'd need to implement a method to notify all methods at once
                // or iterate through all methods and notify them individually
            }
            
            return ResponseEntity.ok("Service reset to template configuration");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to reset service: " + e.getMessage());
        }
    }

    /**
     * PUT /api/services/{serviceName}/changeBehaviour - Update method behavior
     */
    @PutMapping("/services/{serviceName}/changeBehaviour")
    public ResponseEntity<String> updateMethodBehavior(
            @PathVariable String serviceName,
            @RequestParam String className,
            @RequestParam String methodName,
            @RequestParam String behaviourId) {

        try {
            ServiceName service = new ServiceName(serviceName);
            ClassName clazz = new ClassName(className);
            MethodName method = new MethodName(methodName);
            BehaviourId newBehavior = new BehaviourId(behaviourId);

            // Update in the registry (this updates runtime config)
            configRegistry.modifyCurrentBehaviourForMethod(service, clazz, method, newBehavior);
            
            // Notify service instances
            notifyServiceInstances(serviceName, className, methodName, newBehavior);

            return ResponseEntity.ok("Behavior updated successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to update behavior: " + e.getMessage());
        }
    }

    /**
     * Helper method to convert domain entities to DTOs
     */
    private ServiceConfigDTO convertToDTO(ServiceConfig serviceConfig) {
        Map<String, ClassConfigDTO> classConfigs = new HashMap<>();

        for (Map.Entry<ClassName, ClassConfig> classEntry : serviceConfig.getClassConfigurations().entrySet()) {
            ClassConfig classConfig = classEntry.getValue();
            Map<String, MethodConfigDTO> methodConfigs = new HashMap<>();

            for (Map.Entry<MethodName, MethodConfig> methodEntry : classConfig.getMethodsConfigurations().entrySet()) {
                MethodConfig methodConfig = methodEntry.getValue();

                MethodConfigDTO methodDTO = new MethodConfigDTO(
                        methodConfig.getMethodName().getMethodName(),
                        methodConfig.getDefaultBehaviourId().getBehaviourId(),
                        methodConfig.getCurrentBehaviourId().getBehaviourId(),
                        methodConfig.getAvailableBehaviourIds().getBehaviours().stream()
                                .map(id -> id.getBehaviourId())
                                .collect(Collectors.toList()));

                methodConfigs.put(methodConfig.getMethodName().getMethodName(), methodDTO);
            }

            ClassConfigDTO classDTO = new ClassConfigDTO(
                    classConfig.getClassName().getClassName(),
                    methodConfigs);

            classConfigs.put(classConfig.getClassName().getClassName(), classDTO);
        }

        return new ServiceConfigDTO(
                serviceConfig.getServiceName().getServiceName(),
                classConfigs);
    }

    private void notifyServiceInstances(String serviceName, String className, String methodName,
            BehaviourId newBehaviour) {
        System.out.println("Notifying marionette nodes: " + serviceName + " ## " + className + " ## " + methodName + " -> "
                + newBehaviour);

        changeConfigService.notifyAllServiceInstances(namespace, serviceName, className, methodName, newBehaviour);
    }

    // Response DTOs
    public static class ServicesResponse {
        private final Map<String, ServiceConfigDTO> services;
        private final Instant lastDiscovery;
        private final int totalServices;
        private final int unavailableServices;

        public ServicesResponse(Map<String, ServiceConfigDTO> services, Instant lastDiscovery, 
                               int totalServices, int unavailableServices) {
            this.services = services;
            this.lastDiscovery = lastDiscovery;
            this.totalServices = totalServices;
            this.unavailableServices = unavailableServices;
        }

        // Getters
        public Map<String, ServiceConfigDTO> getServices() { return services; }
        public Instant getLastDiscovery() { return lastDiscovery; }
        public int getTotalServices() { return totalServices; }
        public int getUnavailableServices() { return unavailableServices; }
    }

    public static class ServiceDetailResponse {
        private final ServiceConfigDTO runtimeConfig;
        private final ServiceConfigDTO templateConfig;
        private final boolean isModified;
        private final String status;
        private final Instant lastSeen;

        public ServiceDetailResponse(ServiceConfigDTO runtimeConfig, ServiceConfigDTO templateConfig,
                                   boolean isModified, String status, Instant lastSeen) {
            this.runtimeConfig = runtimeConfig;
            this.templateConfig = templateConfig;
            this.isModified = isModified;
            this.status = status;
            this.lastSeen = lastSeen;
        }

        // Getters
        public ServiceConfigDTO getRuntimeConfig() { return runtimeConfig; }
        public ServiceConfigDTO getTemplateConfig() { return templateConfig; }
        public boolean isModified() { return isModified; }
        public String getStatus() { return status; }
        public Instant getLastSeen() { return lastSeen; }
    }

    public static class DiscoveryResponseDTO {
        private final String