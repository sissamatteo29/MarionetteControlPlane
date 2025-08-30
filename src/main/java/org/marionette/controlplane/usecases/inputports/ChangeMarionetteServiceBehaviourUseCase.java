package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.changebehaviour.ChangeMarionetteServiceBehaviourRequest;

public interface ChangeMarionetteServiceBehaviourUseCase {

    public void execute(ChangeMarionetteServiceBehaviourRequest request);
    
}
