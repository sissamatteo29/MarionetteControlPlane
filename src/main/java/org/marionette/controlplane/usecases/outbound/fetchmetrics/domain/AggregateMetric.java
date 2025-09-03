package org.marionette.controlplane.usecases.outbound.fetchmetrics.domain;

public record AggregateMetric (String name, double value, String unit) {}
