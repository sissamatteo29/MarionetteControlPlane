package org.marionette.controlplane.adapters.outbound.fetchconfig.uri;

import static java.util.Objects.requireNonNull;

import java.net.URI;


/**
 * Kubernetes-specific implementation of ServiceURIFactory.
 * Creates service URIs following Kubernetes DNS naming conventions.
 */
public class KubernetesServiceURIFactory implements ServiceURIFactory {
    
    private final String namespace;
    private final String clusterDomain;
    private final String defaultPath;
    
    public KubernetesServiceURIFactory(String namespace, String clusterDomain) {
        this(namespace, clusterDomain, "");
    }
    
    public KubernetesServiceURIFactory(String namespace, String clusterDomain, String defaultPath) {
        requireNonNull(namespace, "Namespace cannot be null");
        requireNonNull(clusterDomain, "Cluster domain cannot be null");
        requireNonNull(defaultPath, "Default path cannot be null");
        
        this.namespace = namespace;
        this.clusterDomain = clusterDomain;
        this.defaultPath = defaultPath;
    }
    
    @Override
    public URI createServiceURI(String serviceName) {
        return createServiceURI(serviceName, defaultPath);
    }
    
    @Override
    public URI createServiceURI(String serviceName, String path) {
        requireNonNull(serviceName, "Service name cannot be null");
        requireNonNull(path, "Path cannot be null");
        
        // Kubernetes DNS format: http://service-name.namespace.svc.cluster-domain/path
        String uri = String.format("http://%s.%s.svc.%s%s", 
            serviceName, namespace, clusterDomain, formatPath(path));
            
        return URI.create(uri);
    }
    
    private String formatPath(String path) {
        if (path.isEmpty()) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
