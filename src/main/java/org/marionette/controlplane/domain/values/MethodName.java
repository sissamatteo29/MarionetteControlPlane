package org.marionette.controlplane.domain.values;


import org.marionette.controlplane.domain.entities.StringValidator;

public class MethodName {

    private final String methodName;

    public MethodName(String methodName) {
        this.methodName = StringValidator.validateStringAndTrim(methodName);
    }

    public String getMethodName() {
        return methodName;
    }
    
}
