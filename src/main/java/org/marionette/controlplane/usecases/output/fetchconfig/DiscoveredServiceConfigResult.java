package org.marionette.controlplane.usecases.output.fetchconfig;

import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public class DiscoveredServiceConfigResult {

    private final ServiceConfigData serviceConfigData;
    private final boolean success;
    private final String errorMessage;

    private DiscoveredServiceConfigResult(ServiceConfigData serviceConfigData, boolean success, String errorMessage) {
        this.serviceConfigData = serviceConfigData;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static DiscoveredServiceConfigResult success(ServiceConfigData serviceConfigData) {
        return new DiscoveredServiceConfigResult(serviceConfigData, true, "");
    }

    public static DiscoveredServiceConfigResult failure(String errorMessage) {
        return new DiscoveredServiceConfigResult(null, false, errorMessage);
    }

    public ServiceConfigData serviceConfigData() {
        if(!success) {
            throw new IllegalAccessError("Trying to access the service configuration data from an unsuccessful response");
        }
        return serviceConfigData;
    }


    public String errorMessage() {
        if(success) {
            throw new IllegalAccessError("Trying to access the error message from an successful response");
        }
        return errorMessage;
    }

    public boolean isSuccessfull() {
        return success;
    }

}
