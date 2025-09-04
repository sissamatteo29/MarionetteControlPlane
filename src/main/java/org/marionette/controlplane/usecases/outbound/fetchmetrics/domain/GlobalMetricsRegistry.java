package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.marionette.controlplane.usecases.domain.ConfigRegistrySnapshot;

public class GlobalMetricsRegistry {

    // String identifiers like conf-1, conf-2...
    private final String keyPattern = "conf-";
    private final Map<String, ConfigRegistrySnapshot> globalConfigs = new ConcurrentHashMap<>();
    private final Map<String, SystemMetricsDataPoint> globalMetrics = new ConcurrentHashMap<>();

    private final AtomicInteger globalConfigCounter = new AtomicInteger(0);

    public synchronized void putSystemMetrics(ConfigRegistrySnapshot systemConfigSnapshot, SystemMetricsDataPoint dataPoint) {
        String identifier = keyPattern + globalConfigCounter.getAndIncrement();
        globalConfigs.put(identifier, systemConfigSnapshot);
        globalMetrics.put(identifier, dataPoint);
    }

    public SystemMetricsDataPoint getSystemDataPoint(int index) {
        return globalMetrics.get(keyPattern + index);
    }
    
    public ConfigRegistrySnapshot getSystemConfig(int index) {
        return globalConfigs.get(keyPattern + index);
    }
    
    
}
