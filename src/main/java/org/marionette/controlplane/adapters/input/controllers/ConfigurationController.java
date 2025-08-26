package org.marionette.controlplane.adapters.input.controllers;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.values.*;
import org.marionette.controlplane.adapters.input.changeconfig.ChangeConfigService;
import org.marionette.controlplane.adapters.input.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.marionette.controlplane.usecases.input.changebehaviour.BehaviourChangeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow React dev server to connect
public class ConfigurationController {

    private final ConfigRegistry configRegistry;
    private final String namespace;
    private final ChangeConfigService changeConfigService;

    public ConfigurationController(ConfigRegistry configRegistry, String namespace,
            ChangeConfigService changeConfigService) {
        this.configRegistry = configRegistry;
        this.namespace = namespace;
        this.changeConfigService = changeConfigService;
    }

    /**
     * GET /api/services - Get all services with their configurations
     * This is what the React frontend calls to populate the UI
     */
    @GetMapping("/services")
    public ResponseEntity<Map<String, ServiceConfigDTO>> getAllServices() {
        Map<String, ServiceConfigDTO> serviceConfigs = new HashMap<>();

        configRegistry.toString();

        Map<ServiceName, ServiceConfig> allServices = configRegistry.getAllServices();

        for (Map.Entry<ServiceName, ServiceConfig> entry : allServices.entrySet()) {
            ServiceConfig serviceConfig = entry.getValue();
            ServiceConfigDTO dto = convertToDTO(serviceConfig);
            serviceConfigs.put(entry.getKey().getServiceName(), dto);
        }

        return ResponseEntity.ok(serviceConfigs);
    }

    /**
     * GET /api/services/{serviceName} - Get specific service configuration
     */
    @GetMapping("/services/{serviceName}")
    public ResponseEntity<ServiceConfigDTO> getService(@PathVariable String serviceName) {
        try {
            ServiceName service = new ServiceName(serviceName);
            ServiceConfig serviceConfig = configRegistry.getServiceConfiguration(service);

            if (serviceConfig == null) {
                return ResponseEntity.notFound().build();
            }

            ServiceConfigDTO dto = convertToDTO(serviceConfig);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT
     * This is called when user changes a behavior in the React UI
     */
    @PutMapping("/services/{serviceName}/changeBehaviour")
    public ResponseEntity<String> updateMethodBehaviorWithParams(
            @PathVariable String serviceName,
            @RequestParam String className,
            @RequestParam String methodName,
            @RequestParam String behaviourId) {

        // URL would be:
        // /api/services/user-service/behavior?className=com.outfit.processor.ImageController&methodName=processImage&behaviourId=low_energy

        try {
            ServiceName service = new ServiceName(serviceName);
            ClassName clazz = new ClassName(className);
            MethodName method = new MethodName(methodName);
            BehaviourId newBehavior = new BehaviourId(behaviourId);

            configRegistry.modifyCurrentBehaviourForMethod(service, clazz, method, newBehavior);
            notifyServiceInstances(serviceName, className, methodName, newBehavior);

            return ResponseEntity.ok("Behavior updated successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update behavior: " + e.getMessage());
        }
    }

    /**
     * Helper method to convert your domain entities to DTOs
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

    // TODO: Method to notify marionette nodes when configuration changes
    private void notifyServiceInstances(String serviceName, String className, String methodName,
            BehaviourId newBehaviour) {
        System.out.println("Notifying marionette nodes: " + serviceName + " ## " + className + " ## " + methodName + " -> "
                + newBehaviour);

                changeConfigService.notifyAllServiceInstances(namespace, serviceName, className, methodName, newBehaviour);

    }
}
