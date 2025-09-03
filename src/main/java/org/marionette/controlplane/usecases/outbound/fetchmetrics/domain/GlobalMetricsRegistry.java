package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalMetricsRegistry {

    // String identifiers like conf-1, conf-2...
    private final String keyPattern = "conf-";
    private final Map<String, SystemMetricsDataPoint> globalMetrics = new ConcurrentHashMap<>();

    private final AtomicInteger globalConfigCounter = new AtomicInteger(0);

    public synchronized void putSystemMetrics(SystemMetricsDataPoint dataPoint) {
        String identifier = keyPattern + globalConfigCounter.getAndIncrement();
        globalMetrics.put(identifier, dataPoint);
    }


    public SystemMetricsDataPoint getSystemDataPoint(int index) {

        return globalMetrics.get(keyPattern + index);

    }
    
    
}
