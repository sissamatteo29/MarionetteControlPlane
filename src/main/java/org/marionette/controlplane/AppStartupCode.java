package org.marionette.controlplane;

import org.marionette.controlplane.usecases.inbound.FullMarionetteServiceConfigDiscoveryUseCase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Component
public class AppStartupCode implements CommandLineRunner {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final FullMarionetteServiceConfigDiscoveryUseCase discoveryUseCase;

    public AppStartupCode(FullMarionetteServiceConfigDiscoveryUseCase discoveryUseCase) {
        requireNonNull(discoveryUseCase, "The use case to discover all marionette service configurations cannot be null");
        this.discoveryUseCase = discoveryUseCase;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting Marionette Control Plane...");
        System.out.println("====================================");
        executor.scheduleAtFixedRate(() -> discoveryUseCase.execute(), 0, 5, TimeUnit.MINUTES);
    }
}