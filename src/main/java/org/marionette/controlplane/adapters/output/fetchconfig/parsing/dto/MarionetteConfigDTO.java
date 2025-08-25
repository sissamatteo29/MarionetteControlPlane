package org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "marionetteConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class MarionetteConfigDTO {
    public String microserviceName;
    public String srcRoot;
    public String injectCodeRoot;

    @XmlElementWrapper(name = "marionetteClasses")
    @XmlElement(name = "marionetteClass")
    public List<MarionetteClassConfigDTO> marionetteClasses;

    public MarionetteConfigDTO() {}

}