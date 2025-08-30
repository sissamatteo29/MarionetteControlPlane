package org.marionette.controlplane.adapters.output.servicediscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marionette.controlplane.usecases.outputports.servicediscovery.FindMarionetteServicesPort;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.util.Config;

public class KubernetesFindServicesAdapter implements FindMarionetteServicesPort {

    private final CoreV1Api coreV1Api;

    public KubernetesFindServicesAdapter(String namespace) throws RuntimeException {
        
        try {
            // Auto-configure from cluster (if running inside K8s) or kubeconfig
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            this.coreV1Api = new CoreV1Api();
        } catch (Exception e) {
            throw new RuntimeException("Impossible to create the KubernetesServiceDiscoveryAdapter");
        }
    }

    @Override
    public List<String> getAllServices() {
        return new ArrayList<>(getMicroserviceEndpoints(coreV1Api).values());
    }


    public Map<String, String> getMicroserviceEndpoints(CoreV1Api api) {
        Map<String, String> endpoints = new HashMap<>();
        
        try {
            // Get all services across all namespaces (or specify your namespace)
            V1ServiceList serviceList = api.listServiceForAllNamespaces(
                null, null, null, null, null, null, null, null, null, null);
            
            System.out.println("=== Discovering Microservice Endpoints ===");
            
            for (V1Service service : serviceList.getItems()) {
                String serviceName = service.getMetadata().getName();
                String namespace = service.getMetadata().getNamespace();
                
                // Skip system services (optional - you might want to include them)
                if (isSystemService(serviceName, namespace)) {
                    continue;
                }
                
                // Build internal cluster endpoint
                if (service.getSpec().getPorts() != null && !service.getSpec().getPorts().isEmpty()) {
                    V1ServicePort port = service.getSpec().getPorts().get(0);
                    
                    // This is the URL you'll use to call this microservice from within the cluster
                    String endpoint = String.format("http://%s.%s.svc.cluster.local:%d", 
                                                   serviceName, namespace, port.getPort());
                    
                    endpoints.put(serviceName, endpoint);
                    
                    System.out.println("Found microservice: " + serviceName);
                    System.out.println("  Namespace: " + namespace);
                    System.out.println("  Internal URL: " + endpoint);
                    System.out.println("  Service Type: " + service.getSpec().getType());
                    
                    // Show all available ports
                    System.out.println("  Available ports:");
                    for (V1ServicePort servicePort : service.getSpec().getPorts()) {
                        System.out.println("    " + servicePort.getName() + ": " + 
                                         servicePort.getPort() + "/" + servicePort.getProtocol());
                    }
                    System.out.println();
                }
            }
        } catch (ApiException e) {
            System.err.println("Exception when getting services: " + e.getResponseBody());
        }
        
        return endpoints;
    }


    // Get microservices in a specific namespace (if you know your namespace)
    public Map<String, String> getMicroserviceEndpointsInNamespace(CoreV1Api api, String namespace) {
        Map<String, String> endpoints = new HashMap<>();
        
        try {
            V1ServiceList serviceList = api.listNamespacedService(
                namespace, null, null, null, null, null, null, null, null, null, null);
            
            System.out.println("=== Microservices in namespace: " + namespace + " ===");
            
            for (V1Service service : serviceList.getItems()) {
                String serviceName = service.getMetadata().getName();
                
                if (service.getSpec().getPorts() != null && !service.getSpec().getPorts().isEmpty()) {
                    V1ServicePort port = service.getSpec().getPorts().get(0);
                    
                    // For same namespace, you can use the short form
                    String endpoint = String.format("http://%s:%d", serviceName, port.getPort());
                    // Or the full form: http://serviceName.namespace.svc.cluster.local:port
                    
                    endpoints.put(serviceName, endpoint);
                    
                    System.out.println("Service: " + serviceName);
                    System.out.println("  Short URL (same namespace): " + endpoint);
                    System.out.println("  Full URL: http://" + serviceName + "." + namespace + ".svc.cluster.local:" + port.getPort());
                    System.out.println();
                }
            }
        } catch (ApiException e) {
            System.err.println("Exception: " + e.getResponseBody());
        }
        
        return endpoints;
    }

    // Helper method to identify system services you might want to skip
    private boolean isSystemService(String serviceName, String namespace) {
        // Skip common Kubernetes system services
        return "kube-system".equals(namespace) || 
               "kube-public".equals(namespace) ||
               "kube-node-lease".equals(namespace) ||
               serviceName.startsWith("kube-") ||
               serviceName.equals("kubernetes");
    }


    // Display the endpoints in a readable format
    public void displayMicroserviceEndpoints(Map<String, String> endpoints) {
        System.out.println("\n=== YOUR MICROSERVICE ENDPOINTS ===");
        System.out.println("Use these URLs to call your microservices from within the cluster:\n");
        
        endpoints.forEach((serviceName, endpoint) -> {
            System.out.println(serviceName + " -> " + endpoint);
        });
        
        System.out.println("\n=== USAGE EXAMPLES ===");
        System.out.println("// In your Java code:");
        endpoints.entrySet().stream().limit(2).forEach(entry -> {
            System.out.println("String " + entry.getKey().toUpperCase().replace("-", "_") + "_URL = \"" + entry.getValue() + "\";");
        });
    }
    



    
}
