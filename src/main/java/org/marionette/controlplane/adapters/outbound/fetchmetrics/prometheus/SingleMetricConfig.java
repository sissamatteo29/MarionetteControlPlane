package org.marionette.controlplane.adapters.outbound.fetchmetrics.prometheus;

public class SingleMetricConfig {

    private final String query;
    private final TimeAggregator timeAggregator;
    private final ServiceAggregator serviceAggregator;


    // UI and visual
    private final String displayName;
    private final String unit;
    private final String description;


    public SingleMetricConfig(String query, TimeAggregator timeAggregator, ServiceAggregator serviceAggregator,
            String displayName, String unit, String description) {
        this.query = query;
        this.timeAggregator = timeAggregator;
        this.serviceAggregator = serviceAggregator;
        this.displayName = displayName;
        this.unit = unit;
        this.description = description;
    }


    @Override
    public String toString() {
        return String.format("%s {\n" +
            "      query: \"%s\"\n" +
            "      timeAggregator: %s\n" +
            "      serviceAggregator: %s\n" +
            "      unit: %s\n" +
            "      description: \"%s\"\n" +
            "    }",
            displayName != null ? displayName : "Unnamed Metric",
            query,
            timeAggregator,
            serviceAggregator,
            unit != null ? unit : "none",
            description != null ? description : "No description");
    }


    public String getQuery() {
        return query;
    }


    public TimeAggregator getTimeAggregator() {
        return timeAggregator;
    }


    public ServiceAggregator getServiceAggregator() {
        return serviceAggregator;
    }


    public String getDisplayName() {
        return displayName;
    }


    public String getUnit() {
        return unit;
    }


    public String getDescription() {
        return description;
    }

    
}
