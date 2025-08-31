package org.marionette.controlplane.adapters.outbound.fetchconfig.uri;

import java.net.URI;

/**
 * Factory interface for creating service URIs based on the deployment environment.
 * Different implementations can handle various cluster types (Kubernetes, static config, etc.)
 */
public interface ServiceURIFactory {
    
    /**
     * Creates a service URI for the given service name.
     * 
     * @param serviceName the name of the service to create URI for
     * @return the constructed ExternalServiceURI
     */
    URI createServiceURI(String serviceName);
    
    /**
     * Creates a service URI for the given service name with a specific path.
     * 
     * @param serviceName the name of the service to create URI for
     * @param path the specific path to append to the service URI
     * @return the constructed ExternalServiceURI
     */
    URI createServiceURI(String serviceName, String path);
}
