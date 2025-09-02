package org.marionette.controlplane.adapters.inbound.controllers;

import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.values.*;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;
import org.marionette.controlplane.usecases.inbound.ChangeMarionetteServiceBehaviourUseCase;
import org.marionette.controlplane.usecases.inbound.ReadAllMarionetteConfigsUseCase;
import org.marionette.controlplane.usecases.inbound.readconfigs.ReadAllMarionetteConfigsResponse;
import org.marionette.controlplane.adapters.inbound.dto.*;
import org.marionette.controlplane.adapters.outbound.changeconfig.ChangeBehaviourRequestDTO;
import org.marionette.controlplane.adapters.outbound.changeconfig.ChangeConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ConfigurationController {

    private final ReadAllMarionetteConfigsUseCase readAllConfigsUseCase;
    private final ChangeMarionetteServiceBehaviourUseCase changeBehaviourUseCase;

    public ConfigurationController(ReadAllMarionetteConfigsUseCase readAllConfigsUseCase,
            ChangeMarionetteServiceBehaviourUseCase changeBehaviourUseCase) {
        this.readAllConfigsUseCase = readAllConfigsUseCase;
        this.changeBehaviourUseCase = changeBehaviourUseCase;
    }

    /**
     * GET /api/services - Get all services with their current runtime
     * configurations
     */
    @GetMapping("/services")
    public ResponseEntity<AllServiceConfigsDTO> getAllServices() {

        ReadAllMarionetteConfigsResponse allConfigs = readAllConfigsUseCase.execute();

        AllServiceConfigsDTO response = mapToDTO(allConfigs);

        return ResponseEntity.ok(response);

    }


    @PostMapping("service/{serviceName}/changeBehaviour")
    public ResponseEntity<?> changeBehaviour(
        @PathVariable String serviceName,
        @RequestBody ChangeBehaviourRequestDTO changeRequestDTO
    ) {
        // Required by API contract
        requireNonNull(serviceName, "The name of the service in the request to change behaviour was absent");

        
        





        return ResponseEntity.ok("success");
    }










    private AllServiceConfigsDTO mapToDTO(ReadAllMarionetteConfigsResponse allConfigs) {
        List<ServiceConfigDTO> serviceDtos = new ArrayList<>();
        for (ServiceConfigData serviceConfigData : allConfigs.serviceConfigs()) {

            serviceDtos.add(
                    new ServiceConfigDTO(serviceConfigData.serviceName(),
                            serviceConfigData.classConfigs().stream().map(
                                    classConfigData -> {
                                        return new ClassConfigDTO(classConfigData.className(),
                                                classConfigData.methodConfigData().stream().map(
                                                        methodConfigData -> {
                                                            System.out.println("DEBUG: Controller mapping MethodConfigData - " +
                                                                "method: " + methodConfigData.methodName() + 
                                                                ", default: " + methodConfigData.defaultBehaviourId() + 
                                                                ", current: " + methodConfigData.currentBehaviourId() + 
                                                                ", available: " + methodConfigData.availableBehaviourIds());
                                                            return new MethodConfigDTO(methodConfigData.methodName(),
                                                                    methodConfigData.defaultBehaviourId(),
                                                                    methodConfigData.currentBehaviourId(),
                                                                    methodConfigData.availableBehaviourIds());
                                                        }).collect(Collectors.toList()));
                                    }).collect(Collectors.toList())));

        }
        return new AllServiceConfigsDTO(serviceDtos);
    }

   

}