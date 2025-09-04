package org.marionette.controlplane.usecases.inbound.abntest.engine;

import java.time.Duration;
import java.util.List;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;
import org.marionette.controlplane.domain.values.ServiceName;
import org.marionette.controlplane.usecases.domain.configsnapshot.SystemConfigurationSnapshot;
import org.marionette.controlplane.usecases.inbound.abntest.domain.GlobalMetricsRegistry;
import org.marionette.controlplane.usecases.inbound.abntest.domain.SingleBehaviourSelection;
import org.marionette.controlplane.usecases.inbound.abntest.domain.SystemBehaviourConfiguration;
import org.marionette.controlplane.usecases.outbound.servicemanipulation.ControlMarionetteServiceBehaviourGateway;

public class UniformAbnTestExecutor implements AbnTestExecutor {

    private final ConfigRegistry globalRegistry;
    private final ControlMarionetteServiceBehaviourGateway controlMarionetteGateway;

    public UniformAbnTestExecutor(ConfigRegistry globalRegistry,
            ControlMarionetteServiceBehaviourGateway controlMarionetteGateway) {
        this.globalRegistry = globalRegistry;
        this.controlMarionetteGateway = controlMarionetteGateway;
    }

    @Override
    public GlobalMetricsRegistry executeAbnTest(List<SystemBehaviourConfiguration> systemConfigurations,
            Duration totalTime) {
        GlobalMetricsRegistry globalMetricsRegistry = new GlobalMetricsRegistry();

        // Compute time slice for each configuration
        Duration timeSlice = computeTimeSlice(totalTime, systemConfigurations.size());

        // MAIN LOOP
        for (SystemBehaviourConfiguration systemBehaviourConfiguration : systemConfigurations) {

            // Apply configuration to globalregistry, TODO: send modificaitons through
            // adapter
            applyConfigurationToSystem(systemBehaviourConfiguration, globalRegistry, controlMarionetteGateway);

            // Acquire snapshot of the current configuration
            SystemConfigurationSnapshot systemConfigurationSnapshot = SystemConfigurationSnapshot.fromConfigRegistry(globalRegistry);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO: fetch metrics stuff
            System.out.println("Fetching metrics...");

        }

        return globalMetricsRegistry;
    }

    private void applyConfigurationToSystem(SystemBehaviourConfiguration systemBehaviourConfiguration,
            ConfigRegistry globalRegistry, ControlMarionetteServiceBehaviourGateway controlMarionetteGateway) {
        
        for(SingleBehaviourSelection behaviourSelection : systemBehaviourConfiguration) {
            // Verify if already applied
            ServiceName serviceName = behaviourSelection.getServiceName();
            ClassName className = behaviourSelection.getClassName();
            MethodName methodName = behaviourSelection.getMethodName();
            BehaviourId selectedBehaviour = behaviourSelection.selectedBehaviour();
            if(!isBehaviourAlreadyApplied(globalRegistry, behaviourSelection)) {
                System.out.println("Applying modification to the method [" + serviceName + ":" + className + ":" + methodName + "] " + "changing to " + selectedBehaviour );
                globalRegistry.modifyCurrentBehaviourForMethod(serviceName, className, methodName, selectedBehaviour);

                // TODO: apply configurations to remote marionette node 
            } else {
                System.out.println("The behaviourId [" + selectedBehaviour + "] was already applied for the method: " + serviceName + ":" + className + ":" + methodName);
            }
        }
    }

    private boolean isBehaviourAlreadyApplied(ConfigRegistry globalRegistry,
            SingleBehaviourSelection behaviourSelection) {

        ServiceName serviceName = behaviourSelection.getServiceName();
        ClassName className = behaviourSelection.getClassName();
        MethodName methodName = behaviourSelection.getMethodName();
        BehaviourId selectedBehaviour = behaviourSelection.selectedBehaviour();

        BehaviourId currentBehaviourInRegistry = globalRegistry.getCurrentBehaviourIdForMethod(serviceName, className,
                methodName);

        return currentBehaviourInRegistry.equals(selectedBehaviour);
    }

    public static Duration computeTimeSlice(Duration totalTime, int configurationsCount) {
        validateInput(totalTime, configurationsCount);

        // Calculate base time slice
        long totalSeconds = totalTime.toSeconds();
        long timeSliceSeconds = totalSeconds / configurationsCount;

        // Handle very short time slices
        if (timeSliceSeconds < 30) {
            System.out.printf("Warning: Time slice would be %d seconds per configuration. " +
                    "Consider using a longer total time or fewer configurations.%n", timeSliceSeconds);

            if (timeSliceSeconds == 0) {
                long recommendedTotalSeconds = configurationsCount * 30; // 30 seconds minimum per config
                System.out.printf("Recommendation: Use at least %d seconds total time for %d configurations.%n",
                        recommendedTotalSeconds, configurationsCount);

                // Use minimum viable time slice
                return Duration.ofSeconds(Math.max(1, totalSeconds / configurationsCount));
            }
        }

        // Handle very long time slices
        if (timeSliceSeconds > 600) { // More than 10 minutes
            System.out.printf("Warning: Time slice would be %d seconds (%.1f minutes) per configuration. " +
                    "This is quite long.%n", timeSliceSeconds, timeSliceSeconds / 60.0);
        }

        return Duration.ofSeconds(timeSliceSeconds);
    }

    private static void validateInput(Duration totalTime, int configurationsCount) {
        if (totalTime == null) {
            throw new IllegalArgumentException("Total time cannot be null");
        }
        if (configurationsCount <= 0) {
            throw new IllegalArgumentException("Configurations count must be positive, got: " + configurationsCount);
        }
        if (totalTime.isNegative() || totalTime.isZero()) {
            throw new IllegalArgumentException("Total time must be positive, got: " + totalTime);
        }
    }

}
