package org.marionette.controlplane.adapters.parsing.dto;

import java.util.List;

public class MarionetteConfig {
    public String microserviceName;
    public String srcRoot;
    public String injectCodeRoot;

    public List<MarionetteClassConfig> marionetteClasses;

}