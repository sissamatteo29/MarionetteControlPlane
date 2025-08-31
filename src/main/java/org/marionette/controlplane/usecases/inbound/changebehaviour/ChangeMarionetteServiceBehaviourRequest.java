package org.marionette.controlplane.usecases.inbound.changebehaviour;

public record ChangeMarionetteServiceBehaviourRequest (
    String serviceName, 
    String className, 
    String methodName, 
    String newBehaviourId
    ) {}
