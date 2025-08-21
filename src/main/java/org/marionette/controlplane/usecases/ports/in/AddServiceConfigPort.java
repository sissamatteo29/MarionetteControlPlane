package org.marionette.controlplane.usecases.ports.in;

import org.marionette.controlplane.usecases.addserviceconfig.AddServiceConfigRequest;

public interface AddServiceConfigPort {

    public void handle(AddServiceConfigRequest request);
    
}
