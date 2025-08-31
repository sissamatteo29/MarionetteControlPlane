package org.marionette.controlplane.adapters.inbound.dto;

import java.util.List;

// DTO for Method Configuration
public class MethodConfigDTO {
    private String methodName;
    private String defaultBehaviourId;
    private String currentBehaviourId;
    private List<String> availableBehaviourIds;

    // Constructors
    public MethodConfigDTO() {}
    
    public MethodConfigDTO(String methodName, String defaultBehaviourId, 
                          String currentBehaviourId, List<String> availableBehaviourIds) {
        this.methodName = methodName;
        this.defaultBehaviourId = defaultBehaviourId;
        this.currentBehaviourId = currentBehaviourId;
        this.availableBehaviourIds = availableBehaviourIds;
    }

    // Getters and Setters
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    
    public String getDefaultBehaviourId() { return defaultBehaviourId; }
    public void setDefaultBehaviourId(String defaultBehaviourId) { this.defaultBehaviourId = defaultBehaviourId; }
    
    public String getCurrentBehaviourId() { return currentBehaviourId; }
    public void setCurrentBehaviourId(String currentBehaviourId) { this.currentBehaviourId = currentBehaviourId; }
    
    public List<String> getAvailableBehaviourIds() { return availableBehaviourIds; }
    public void setAvailableBehaviourIds(List<String> availableBehaviourIds) { this.availableBehaviourIds = availableBehaviourIds; }
}