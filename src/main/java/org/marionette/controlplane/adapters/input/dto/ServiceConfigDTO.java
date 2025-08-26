package org.marionette.controlplane.adapters.input.dto;

import java.util.Map;

class ServiceConfigDTO {
    private String serviceName;
    private Map<String, ClassConfigDTO> classes;

    public ServiceConfigDTO() {}
    
    public ServiceConfigDTO(String serviceName, Map<String, ClassConfigDTO> classes) {
        this.serviceName = serviceName;
        this.classes = classes;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public Map<String, ClassConfigDTO> getClasses() { return classes; }
    public void setClasses(Map<String, ClassConfigDTO> classes) { this.classes = classes; }
}
