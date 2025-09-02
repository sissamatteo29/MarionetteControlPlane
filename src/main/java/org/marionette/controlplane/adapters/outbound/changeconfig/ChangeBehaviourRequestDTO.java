package org.marionette.controlplane.adapters.outbound.changeconfig;

import static java.util.Objects.requireNonNull;

public record ChangeBehaviourRequestDTO (String className, String methodName, String newBehaviourId) {

    public ChangeBehaviourRequestDTO {

        // Required fields from API contract
        requireNonNull(className, "The class name in the request to modify behaviour was not present");
        requireNonNull(methodName, "The method name in the request to modify behaviour was not present");
        requireNonNull(newBehaviourId, "The new behaviour id in the request to modify behaviour was not present");
        
    }
}
