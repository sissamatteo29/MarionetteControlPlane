package org.marionette.controlplane.usecases.inbound.abntest.combinations;

import java.util.List;

import org.marionette.controlplane.domain.entities.ConfigRegistry;
import org.marionette.controlplane.usecases.inbound.abntest.domain.VariationPoint;

public class VariationPointsExtractor {

    private final ConfigRegistry globalRegistry;

    public VariationPointsExtractor(ConfigRegistry globalRegistry) {
        this.globalRegistry = globalRegistry;
    }

    public List<VariationPoint> extractAllVariationPoints() {

        System.out.println("\n== EXTRACTING VARIATION POINTS ==");
        System.out.println("The current status of the config registry is: " + globalRegistry);
        
        for()






    }
    
}
