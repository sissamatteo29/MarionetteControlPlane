package org.marionette.controlplane;

import org.marionette.controlplane.adapters.outbound.servicediscovery.ServiceDiscoveryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AppStartupCode implements CommandLineRunner {

    private final ServiceDiscoveryService discoveryService;

    public AppStartupCode(ServiceDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting Marionette Control Plane...");
        System.out.println("====================================");
        
        // Perform initial discovery asynchronously
        // This will populate the registry with template configurations for all discovered services
        CompletableFuture<ServiceDiscoveryService.DiscoveryResult> initialDiscovery = 
            discoveryService.performFullDiscovery();
        
        // Don't block startup - let discovery happen in background
        initialDiscovery.thenAccept(result -> {
            System.out.println("✅ Initial service discovery completed:");
            System.out.println("   - Total services found: " + result.getTotalServices());
            System.out.println("   - New services discovered: " + result.getNewServices());
            System.out.println("   - Configurations fetched: " + result.getConfigsFetched());
            System.out.println("   - Unavailable services: " + result.getUnavailableServices());
            System.out.println("====================================");
            System.out.println("🎯 Marionette Control Plane is ready!");
            System.out.println("   - Web UI: http://localhost:8080 (if port-forwarded)");
            System.out.println("   - API: http://localhost:8080/api/services");
            System.out.println("   - Health: http://localhost:8080/actuator/health");
        }).exceptionally(throwable -> {
            System.err.println("❌ Initial discovery failed: " + throwable.getMessage());
            System.out.println("🔄 Services can still be discovered manually through the UI");
            return null;
        });
        
        System.out.println("📡 Background service discovery started...");
    }
}