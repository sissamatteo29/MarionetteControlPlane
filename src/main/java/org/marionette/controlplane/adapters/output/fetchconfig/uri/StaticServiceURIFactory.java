package org.marionette.controlplane.adapters.output.fetchconfig.uri;

import java.util.Map;

import static java.util.Objects.requireNonNull;

import java.net.URI;

/**
 * Static configuration implementation of ServiceURIFactory.
 * Uses predefined service endpoints from configuration files or environment variables.
 */
public class StaticServiceURIFactory implements ServiceURIFactory {
    
    private final Map<String, String> serviceEndpoints;
    private final String defaultPath;
    
    public StaticServiceURIFactory(Map<String, String> serviceEndpoints) {
        this(serviceEndpoints, "");
    }
    
    public StaticServiceURIFactory(Map<String, String> serviceEndpoints, String defaultPath) {
        requireNonNull(serviceEndpoints, "Service endpoints map cannot be null");
        requireNonNull(defaultPath, "Default path cannot be null");
        
        this.serviceEndpoints = Map.copyOf(serviceEndpoints); // Create immutable copy
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
        
        String baseUrl = serviceEndpoints.get(serviceName);
        if (baseUrl == null) {
            throw new IllegalArgumentException("No endpoint configured for service: " + serviceName);
        }
        
        String fullUri = baseUrl + formatPath(path);
        return URI.create(fullUri);
    }
    
    private String formatPath(String path) {
        if (path.isEmpty()) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
