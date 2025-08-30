package org.marionette.controlplane.usecases.outputports.fetchconfig;

public interface NodeConfigGateway {

    public DiscoveredServiceConfigResult fetchConfiguration(String serviceName);

}