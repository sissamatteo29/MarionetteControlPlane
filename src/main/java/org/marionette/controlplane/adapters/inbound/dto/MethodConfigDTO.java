package org.marionette.controlplane.adapters.inbound.dto;

import java.util.List;

// DTO for Method Configuration
public record MethodConfigDTO (
    String methodName,
    String defaultBehaviourId,
    String currentBehaviourId,
    List<String> availableBehaviourIds) {

    public MethodConfigDTO {
        availableBehaviourIds = List.copyOf(availableBehaviourIds());
    }
}