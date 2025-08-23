package org.marionette.controlplane.usecases.addserviceconfig.request;

import java.util.List;

public record ClassConfigData (String className, List<MethodConfigData> methodConfigData) {

    public ClassConfigData {
        methodConfigData = List.copyOf(methodConfigData);
    }

}
