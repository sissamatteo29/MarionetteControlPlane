package org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class GenericClassConfigDTO {
    
    public String path;

    @XmlElementWrapper(name = "behaviours")
    @XmlElement(name = "behaviour")
    public List<BehaviourConfigDTO> behaviours;

    public GenericClassConfigDTO() {}

}
