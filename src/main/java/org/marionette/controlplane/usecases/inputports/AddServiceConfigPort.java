package org.marionette.controlplane.usecases.inputports;

import org.marionette.controlplane.usecases.inputports.addserviceconfig.AddServiceConfigRequest;

public interface AddServiceConfigPort {

    public void execute(AddServiceConfigRequest request);
    
}
