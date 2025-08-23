package org.marionette.controlplane.usecases.fetchconfig;

import org.marionette.controlplane.usecases.ports.out.clusterconfig.ExternalServiceURI;

import static java.util.Objects.requireNonNull;

import java.net.URI;

public class KubernetesFetchExternalConfigUseCase extends AFetchExternalConfigUseCase {

    private final String namespace;
    private final String clusterDomain;

    public KubernetesFetchExternalConfigUseCase(String namespace, String clusterDomain) {

        requireNonNull(namespace, "Namespace cannot be null");
        requireNonNull(clusterDomain, "Cluster domain cannot be null");
        
        this.namespace = namespace;
        this.clusterDomain = clusterDomain;
    }

    @Override
    protected ExternalServiceURI createServiceURI(String serviceName) {
        return new ExternalServiceURI(
                URI.create(
                        String.format("http://%s.%s.scv.%s/%s", serviceName, namespace, clusterDomain, internalPath)));
    }
    
}
