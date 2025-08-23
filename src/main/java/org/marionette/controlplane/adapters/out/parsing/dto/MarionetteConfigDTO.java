package org.marionette.controlplane.adapters.out.parsing.dto;

import java.util.List;

public class MarionetteConfigDTO {
    public String microserviceName;
    public String srcRoot;
    public String injectCodeRoot;

    public List<MarionetteClassConfigDTO> marionetteClasses;

}