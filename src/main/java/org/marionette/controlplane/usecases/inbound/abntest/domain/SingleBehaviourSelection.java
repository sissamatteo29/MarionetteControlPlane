package org.marionette.controlplane.usecases.inbound.abntest.domain;

import org.marionette.controlplane.domain.values.BehaviourId;

public record SingleBehaviourSelection(VariationPoint variationPoint, BehaviourId behaviourId) {
    
    public boolean isForService(String serviceName) {
        return variationPoint.serviceName().getServiceName().equals(serviceName);
    }
    
    public boolean isForClass(String className) {
        return variationPoint.className().getClassName().equals(className);
    }
    
    public boolean isForMethod(String methodName) {
        return variationPoint.methodName().getMethodName().equals(methodName);
    }
    
    public String getFullMethodPath() {
        return String.format("%s.%s.%s", 
            variationPoint.serviceName().getServiceName(),
            variationPoint.className().getClassName(),
            variationPoint.methodName().getMethodName());
    }
}