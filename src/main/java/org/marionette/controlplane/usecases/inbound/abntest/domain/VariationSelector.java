package org.marionette.controlplane.usecases.inbound.abntest.domain;

import java.util.HashMap;
import java.util.Map;

import org.marionette.controlplane.domain.values.BehaviourId;

/**
 * Represents the selection of behaviours for VarianionPoints
 */
public class VariationSelector {

    private final Map<VariationPoint, BehaviourId> selections = new HashMap<>();

    public Map<VariationPoint, BehaviourId> getSelections() {
        return Map.copyOf(selections);
    }

    public void putSelection(VariationPoint variation, BehaviourId selection) {
        selections.put(variation, selection);
    }

    public BehaviourId getSelection(VariationPoint variation) {
        return selections.get(variation);
    }
    
}
