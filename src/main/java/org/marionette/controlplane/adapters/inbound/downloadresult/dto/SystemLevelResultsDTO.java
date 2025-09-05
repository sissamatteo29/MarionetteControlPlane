package org.marionette.controlplane.adapters.inbound.downloadresult.dto;

import java.util.List;

public record SystemLevelResultsDTO (
    List<MetricValueDTO> systemResults,
    List<ServiceLevelResultsDTO> serviceResults
) {}
