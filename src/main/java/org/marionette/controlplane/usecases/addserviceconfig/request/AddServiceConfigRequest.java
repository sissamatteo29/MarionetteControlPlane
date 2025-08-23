package org.marionette.controlplane.usecases.addserviceconfig.request;

import java.util.List;

public record AddServiceConfigRequest (String serviceName, List<ClassConfigData> classConfigData) {

    public AddServiceConfigRequest {

        classConfigData = List.copyOf(classConfigData);     // Unmodifiable data

    }
    
}
