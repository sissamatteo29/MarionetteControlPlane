package org.marionette.controlplane.adapters.inbound.dto;

import java.util.Map;

public class ClassConfigDTO {
    private String className;
    private Map<String, MethodConfigDTO> methods;

    public ClassConfigDTO() {}
    
    public ClassConfigDTO(String className, Map<String, MethodConfigDTO> methods) {
        this.className = className;
        this.methods = methods;
    }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public Map<String, MethodConfigDTO> getMethods() { return methods; }
    public void setMethods(Map<String, MethodConfigDTO> methods) { this.methods = methods; }
}
