package org.marionette.controlplane.usecases.outputports.servicemanipulation;

public interface ControlMarionetteServiceBehaviourGateway {

    public void changeMarionetteServiceBehaviour(String serviceEndpoint, ChangeBehaviourData changeBehaviourData);
    
}
