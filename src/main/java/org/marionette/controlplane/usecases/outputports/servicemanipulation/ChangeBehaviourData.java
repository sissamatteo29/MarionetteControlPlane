package org.marionette.controlplane.usecases.outputports.servicemanipulation;

public record ChangeBehaviourData (String className, String methodName, String newBehaviourId) {}
