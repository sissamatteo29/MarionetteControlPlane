package org.marionette.controlplane.usecases.ports.in;

import org.marionette.controlplane.usecases.addserviceconfig.request.AddServiceConfigRequest;

public interface AddServiceConfigPort {

    public void handle(AddServiceConfigRequest request);
    
}
