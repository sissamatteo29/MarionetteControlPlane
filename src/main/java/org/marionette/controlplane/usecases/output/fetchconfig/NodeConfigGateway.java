package org.marionette.controlplane.usecases.output.fetchconfig;

public interface NodeConfigGateway {

    public DiscoveredServiceConfigResult fetchConfiguration(String serviceName);

}