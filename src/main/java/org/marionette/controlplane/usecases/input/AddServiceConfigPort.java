package org.marionette.controlplane.usecases.input;

import org.marionette.controlplane.usecases.input.addserviceconfig.AddServiceConfigRequest;

public interface AddServiceConfigPort {

    public void execute(AddServiceConfigRequest request);
    
}
