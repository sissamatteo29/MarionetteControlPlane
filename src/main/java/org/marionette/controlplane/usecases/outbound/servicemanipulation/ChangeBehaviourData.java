package org.marionette.controlplane.usecases.outbound.servicemanipulation;

public record ChangeBehaviourData (String className, String methodName, String newBehaviourId) {}
