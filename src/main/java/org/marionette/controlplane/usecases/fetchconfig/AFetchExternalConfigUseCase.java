package org.marionette.controlplane.usecases.fetchconfig;

import org.marionette.controlplane.usecases.ports.out.clusterconfig.ExternalServiceURI;

import static java.util.Objects.requireNonNull;

public abstract class AFetchExternalConfigUseCase {

    private final String internalServerPath;

    public AFetchExternalConfigUseCase() {
        this.internalServerPath = "";
    }

    public AFetchExternalConfigUseCase(String internalServerPath) {
        requireNonNull(internalServerPath, "The internal server path used to build the use case is null");

        this.internalServerPath = internalServerPath;
    }

    public void fetchAllConfigurations() {




    }


    protected abstract ExternalServiceURI createServiceURI(String serviceName);
    
}
