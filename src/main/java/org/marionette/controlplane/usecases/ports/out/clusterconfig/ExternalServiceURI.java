package org.marionette.controlplane.usecases.ports.out.clusterconfig;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class ExternalServiceURI {

    private final URI uri;

    private ExternalServiceURI(URI uri) {
        requireNonNull(uri, "The uri reference cannot be null");

        this.uri = uri;
    }

    public ExternalServiceURI ofKubernetes(String serviceName, String namespace, String clusterDomain,
            String internalPath) {
        return new ExternalServiceURI(
                URI.create(
                        String.format("http://%s.%s.scv.%s/%s", serviceName, namespace, clusterDomain, internalPath)));
    }

    public ExternalServiceURI fromString(String uri) {
        return new ExternalServiceURI(URI.create(uri));
    }

}
