package org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto;

import java.util.List;

public class MarionetteMethodConfigDTO {

    private String currentBehaviourId;
    private List<String> availableBehaviourIds;
    public String getCurrentBehaviourId() {
        return currentBehaviourId;
    }
    public void setCurrentBehaviourId(String currentBehaviourId) {
        this.currentBehaviourId = currentBehaviourId;
    }
    public List<String> getAvailableBehaviourIds() {
        return availableBehaviourIds;
    }
    public void setAvailableBehaviourIds(List<String> availableBehaviourIds) {
        this.availableBehaviourIds = availableBehaviourIds;
    }

}
