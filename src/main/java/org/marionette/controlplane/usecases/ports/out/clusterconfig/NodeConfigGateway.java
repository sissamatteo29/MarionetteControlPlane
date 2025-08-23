package org.marionette.controlplane.usecases.ports.out.clusterconfig;

public interface NodeConfigGateway {


    public DiscoveredServiceConfigResult fetchConfiguration(ExternalServiceURI uri);



}