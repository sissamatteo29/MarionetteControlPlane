package org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class MarionetteClassConfigDTO {

    public GenericClassConfigDTO originalClass;

    @XmlElementWrapper(name = "variantClasses")
    @XmlElement(name = "variantClass")
    public List<GenericClassConfigDTO> variantClasses;

    public MarionetteClassConfigDTO() {}
    
}
