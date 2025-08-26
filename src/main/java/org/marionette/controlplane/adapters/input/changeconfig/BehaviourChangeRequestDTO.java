package org.marionette.controlplane.adapters.input.changeconfig;

public class BehaviourChangeRequestDTO {

    private String className;

    private String methodName;

    private String behaviourId;

    // Getters and setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBehaviourId() {
        return behaviourId;
    }

    public void setBehaviourId(String behaviourId) {
        this.behaviourId = behaviourId;
    }
}
