package org.marionette.controlplane.usecases.inbound.abntest.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        if (selections.isEmpty()) {
            return "VariationSelector [no selections]";
        }

        String selectionsStr = selections.entrySet().stream()
                .map(entry -> {
                    VariationPoint vp = entry.getKey();
                    BehaviourId behaviour = entry.getValue();
                    return String.format("    %s.%s.%s -> %s",
                            vp.serviceName(),
                            vp.className(),
                            vp.methodName(),
                            behaviour.getBehaviourId());
                })
                .collect(Collectors.joining("\n"));

        return String.format("VariationSelector [\n%s\n]", selectionsStr);
    }

}
