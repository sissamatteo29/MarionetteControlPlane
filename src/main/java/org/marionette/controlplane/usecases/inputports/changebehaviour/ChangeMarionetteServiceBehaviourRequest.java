package org.marionette.controlplane.usecases.inputports.changebehaviour;

public record ChangeMarionetteServiceBehaviourRequest (
    String serviceName, 
    String className, 
    String methodName, 
    String newBehaviourId
    ) {}
