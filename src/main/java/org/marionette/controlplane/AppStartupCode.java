package org.marionette.controlplane;

import org.marionette.controlplane.usecases.inbound.AbnTestAllSystemConfigurationsUseCase;
import org.marionette.controlplane.usecases.inbound.FullMarionetteServiceConfigDiscoveryUseCase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Component
public class AppStartupCode implements CommandLineRunner {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final FullMarionetteServiceConfigDiscoveryUseCase discoveryUseCase;
    private final AbnTestAllSystemConfigurationsUseCase abnTestUseCase;

    public AppStartupCode(FullMarionetteServiceConfigDiscoveryUseCase discoveryUseCase,
                         AbnTestAllSystemConfigurationsUseCase abnTestUseCase) {
        requireNonNull(discoveryUseCase, "The use case to discover all marionette service configurations cannot be null");
        requireNonNull(abnTestUseCase, "The A/B test use case cannot be null");
        this.discoveryUseCase = discoveryUseCase;
        this.abnTestUseCase = abnTestUseCase;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting Marionette Control Plane...");
        System.out.println("====================================");
        
        // Start the discovery process immediately and repeat every 5 minutes
        executor.scheduleAtFixedRate(() -> {
            try {
                System.out.println("🔍 Running service discovery...");
                discoveryUseCase.execute();
                System.out.println("✅ Service discovery completed");
                
                // Immediately start A/B testing after successful discovery
                System.out.println("🧪 Starting A/B tests on system configurations...");
                abnTestUseCase.execute();
                System.out.println("✅ A/B testing completed");
                
            } catch (Exception e) {
                System.err.println("❌ Error during discovery or A/B testing: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);
        
        System.out.println("📅 Scheduled tasks:");
        System.out.println("   - Service Discovery + A/B Testing: every 5 minutes");
        System.out.println("   - A/B testing runs immediately after each discovery completion");
    }
}