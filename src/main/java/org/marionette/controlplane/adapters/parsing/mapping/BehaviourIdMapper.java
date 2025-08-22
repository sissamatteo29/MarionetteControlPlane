package org.marionette.controlplane.adapters.parsing.mapping;

import org.marionette.controlplane.adapters.parsing.dto.BehaviourConfigDTO;
import org.marionette.controlplane.domain.values.BehaviourId;

public class BehaviourIdMapper {

    public static BehaviourId toDomain(BehaviourConfigDTO behaviourConfigDTO) {
        return new BehaviourId(behaviourConfigDTO.behaviourName);
    }
    
}
