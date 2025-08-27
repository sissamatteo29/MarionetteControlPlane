package org.marionette.controlplane.adapters.output.servicediscovery;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ScheduledDiscoveryService {

    private final ServiceDiscoveryService discoveryService;
    private volatile boolean discoveryEnabled = true;

    public ScheduledDiscoveryService(ServiceDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    /**
     * Periodic quick discovery every 5 minutes
     * Only checks service availability, doesn't re-fetch configurations
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void periodicQuickDiscovery() {
        if (!discoveryEnabled) {
            return;
        }

        System.out.println("🔄 Running periodic service discovery...");
        
        CompletableFuture<ServiceDiscoveryService.DiscoveryResult> future = 
            discoveryService.performQuickDiscovery();
        
        future.thenAccept(result -> {
            if (result.getNewServices() > 0 || result.getUnavailableServices() > 0) {
                System.out.println("📊 Discovery update: " + result);
            }
        }).exceptionally(throwable -> {
            System.err.println("⚠️ Periodic discovery failed: " + throwable.getMessage());
            return null;
        });
    }

    /**
     * Full discovery every 30 minutes to catch new services
     * This will fetch template configurations for any new services found
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void periodicFullDiscovery() {
        if (!discoveryEnabled) {
            return;
        }

        System.out.println("🔍 Running periodic full discovery...");
        
        CompletableFuture<ServiceDiscoveryService.DiscoveryResult> future = 
            discoveryService.performFullDiscovery();
        
        future.thenAccept(result -> {
            if (result.getNewServices() > 0) {
                System.out.println("🆕 Found new services: " + result);
            }
        }).exceptionally(throwable -> {
            System.err.println("⚠️ Periodic full discovery failed: " + throwable.getMessage());
            return null;
        });
    }

    /**
     * Enable/disable automatic discovery (useful for debugging or maintenance)
     */
    public void setDiscoveryEnabled(boolean enabled) {
        this.discoveryEnabled = enabled;
        System.out.println("🔧 Automatic discovery " + (enabled ? "enabled" : "disabled"));
    }

    public boolean isDiscoveryEnabled() {
        return discoveryEnabled;
    }
}