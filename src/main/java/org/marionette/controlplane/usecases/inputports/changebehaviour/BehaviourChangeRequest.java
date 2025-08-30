package org.marionette.controlplane.usecases.inputports.changebehaviour;

public class BehaviourChangeRequest {
    private String newBehaviourId;

    public BehaviourChangeRequest() {}
    
    public BehaviourChangeRequest(String newBehaviourId) {
        this.newBehaviourId = newBehaviourId;
    }

    public String getNewBehaviourId() { return newBehaviourId; }
    public void setNewBehaviourId(String newBehaviourId) { this.newBehaviourId = newBehaviourId; }
}
