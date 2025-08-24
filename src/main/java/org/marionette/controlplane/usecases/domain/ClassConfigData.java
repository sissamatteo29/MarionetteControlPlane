package org.marionette.controlplane.usecases.domain;

import java.util.List;

public record ClassConfigData (String className, List<MethodConfigData> methodConfigData) {

    public ClassConfigData {
        methodConfigData = List.copyOf(methodConfigData);
    }

}
