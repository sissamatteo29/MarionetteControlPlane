package org.marionette.controlplane.domain.values;

import org.marionette.controlplane.domain.entities.StringValidator;

public class ClassName {

    private final String className;

    public ClassName(String className) {
        this.className = StringValidator.validateStringAndTrim(className);
    }

    public String getClassName() {
        return className;
    }

    
}
