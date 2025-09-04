package org.marionette.controlplane.usecases.inbound.abntest.domain;

import java.util.Set;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;
import org.marionette.controlplane.domain.values.ServiceName;

/**
 * Supporting data structure to represent a possible variation in the whole system
 */
public record VariationPoint (ServiceName serviceName, ClassName className, MethodName methodName, Set<BehaviourId> behaviours) {
    public VariationPoint {
        behaviours = Set.copyOf(behaviours());
    }
}
