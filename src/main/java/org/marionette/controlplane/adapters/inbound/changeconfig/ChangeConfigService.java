package org.marionette.controlplane.adapters.inbound.changeconfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Config;

@Service
public class ChangeConfigService {

    private final CoreV1Api coreV1Api;
    private final RestTemplate restTemplate;
    
    @Value("${kubernetes.service.host:https://kubernetes.default.svc}")
    private String kubernetesHost;

    public ChangeConfigService() {
        try {
            // Try to use in-cluster config first, fall back to default config
            ApiClient client;
            try {
                // This works when running inside the cluster
                client = Config.fromCluster();
                System.out.println("Using in-cluster Kubernetes configuration");
            } catch (Exception e) {
                // Fall back to default config (for local development)
                client = Config.defaultClient();
                System.out.println("Using default Kubernetes configuration");
            }
            
            // Set reasonable timeouts
            client.setConnectTimeout(30_000);
            client.setReadTimeout(30_000);
            client.setWriteTimeout(30_000);
            
            Configuration.setDefaultApiClient(client);
            this.coreV1Api = new CoreV1Api(client); // Pass the configured client
            this.restTemplate = new RestTemplate();
            
        } catch (Exception e) {
            System.err.println("Error building ChangeConfigService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible to build the object ChangeConfigService", e);
        }
    }

    public void notifyAllServiceInstances(String namespace, String serviceName, String className, String methodName,
            BehaviourId newBehavior) {
        try {
            System.out.println("Looking up service: " + serviceName + " in namespace: " + namespace);

            // Step 1: Get the Service to find its selector
            V1Service service = coreV1Api.readNamespacedService(serviceName, namespace, null);
            Map<String, String> selector = service.getSpec().getSelector();

            if (selector == null || selector.isEmpty()) {
                System.err.println("Service " + serviceName + " has no selector");
                return;
            }

            // Step 2: Convert selector map to label selector string
            String labelSelector = selector.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(","));

            System.out.println("Using label selector from service: " + labelSelector);

            // Step 3: Find pods using the service's selector
            V1PodList podList = coreV1Api.listNamespacedPod(
                    namespace, null, null, null, null,
                    labelSelector, // Use the service's own selector
                    null, null, null, null, null);

            List<V1Pod> runningPods = podList.getItems().stream()
                    .filter(pod -> "Running".equals(pod.getStatus().getPhase()))
                    .filter(pod -> pod.getStatus().getPodIP() != null)
                    .collect(Collectors.toList());

            System.out.println("Found " + runningPods.size() + " running pods for service " + serviceName);

            if (runningPods.isEmpty()) {
                System.err.println("No running pods found for service: " + serviceName);
                return;
            }

            // Step 4: Notify each pod
            notifyPods(runningPods, className, methodName, newBehavior);

        } catch (ApiException e) {
            System.err.println("Kubernetes API error - Code: " + e.getCode() + ", Message: " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBody());
            
            if (e.getCode() == 404) {
                System.err.println("Service " + serviceName + " not found in namespace " + namespace);
            } else if (e.getCode() == 403) {
                System.err.println("Permission denied. Check RBAC permissions for the service account");
            }
        } catch (Exception e) {
            System.err.println("Error discovering pods for service " + serviceName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void notifyPods(List<V1Pod> pods, String className, String methodName, BehaviourId newBehavior) {
        BehaviourChangeRequestDTO request = new BehaviourChangeRequestDTO();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setBehaviourId(newBehavior.getBehaviourId());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BehaviourChangeRequestDTO> entity = new HttpEntity<>(request, headers);
        
        // Notify all pods in parallel
        List<CompletableFuture<String>> futures = pods.stream()
            .map(pod -> CompletableFuture.supplyAsync(() -> notifySinglePod(pod, entity)))
            .collect(Collectors.toList());
        
        // Wait for all and report results
        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        
        long successCount = results.stream().filter("Success"::equals).count();
        System.out.println("Notification complete: " + successCount + "/" + results.size() + " pods updated");
    }

    private String notifySinglePod(V1Pod pod, HttpEntity<BehaviourChangeRequestDTO> request) {
        String podIP = pod.getStatus().getPodIP();
        String podName = pod.getMetadata().getName();
        
        try {
            String podUrl = "http://" + podIP + ":8080/api/changeBehaviour";
            System.out.println("Notifying pod: " + podName + " at URL: " + podUrl);
            
            String response = restTemplate.postForObject(podUrl, request, String.class);
            System.out.println("Pod " + podName + " (" + podIP + "): " + response);
            return response != null ? response : "Success";
            
        } catch (Exception e) {
            System.err.println("Failed to notify pod " + podName + ": " + e.getMessage());
            return "Failed";
        }
    }
}