package org.marionette.controlplane.usecases.addserviceconfig.request;

import java.util.List;

public record MethodConfigData (String methodName, String originalBehaviourId, List<String> availableBehaviourIds) {

    public MethodConfigData {
        availableBehaviourIds = List.copyOf(availableBehaviourIds);
    }

}
