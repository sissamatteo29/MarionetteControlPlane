package org.marionette.controlplane.adapters.inbound.changeconfig;

import org.marionette.controlplane.usecases.outbound.servicemanipulation.ChangeBehaviourData;
import org.marionette.controlplane.usecases.outbound.servicemanipulation.ControlMarionetteServiceBehaviourGateway;

public class ControlMarionetteServiceBehaviourAdapter implements ControlMarionetteServiceBehaviourGateway {

    @Override
    public void changeMarionetteServiceBehaviour(String serviceEndpoint, ChangeBehaviourData changeBehaviourData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeMarionetteServiceBehaviour'");
    }

}
