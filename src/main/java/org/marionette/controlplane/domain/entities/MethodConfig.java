package org.marionette.controlplane.domain.entities;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.BehaviourIdSet;
import org.marionette.controlplane.domain.values.MethodName;

/***
 * Entity
 */
public class MethodConfig {

    private final MethodName methodName;
    private final BehaviourId defaultBehaviourId;
    private final BehaviourIdSet availableBehaviourIds;
    
    private BehaviourId currentBehaviourId;
    
    
    private MethodConfig(MethodName methodName, BehaviourId defaultBehaviourId, BehaviourId currentBehaviourId,
            BehaviourIdSet availableBehaviourIds) {
        this.methodName = requireNonNull(methodName, "The method name for the MethodConfig object is a null value");
        this.defaultBehaviourId = requireNonNull(defaultBehaviourId,
                "The default behaviour id for the MethodConfig object is a null value");
        this.currentBehaviourId = requireNonNull(currentBehaviourId,
                "The current behaviour id for the MethodConfig object is a null value");
        this.availableBehaviourIds = requireNonNull(availableBehaviourIds,
        "The set of available behaviour ids for the MethodConfig object is a null value");
        
        if (availableBehaviourIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "The list of available behaviour ids in the MethodConfig object contained 0 elements");
        }
        if (!availableBehaviourIds.contains(defaultBehaviourId)) {
            throw new IllegalArgumentException(
                    "The list of available behaviour ids does not contain the default behaviour id set for the method "
                            + methodName);
        }
        if (!availableBehaviourIds.contains(currentBehaviourId)) {
            throw new IllegalArgumentException(
                    "The list of available behaviour ids does not contain the current behaviour id set for the method "
                            + methodName);
        }
    }

    public static MethodConfig of(String methodName, String defaultBehaviourId, String currentBehaviourId,
            Collection<String> availableBehaviourIds) {

        MethodName name = new MethodName(methodName);
        BehaviourId defaultBehaviour = new BehaviourId(defaultBehaviourId);
        BehaviourId currentBehaviour = new BehaviourId(currentBehaviourId);
        BehaviourIdSet availableBehaviours = BehaviourIdSet.fromStringCollection(availableBehaviourIds);

        return new MethodConfig(name, defaultBehaviour, currentBehaviour, availableBehaviours);

    }


    public void setCurrentBehaviourId(BehaviourId currentBehaviourId) {
        if(!availableBehaviourIds.contains(currentBehaviourId)) {
            throw new IllegalArgumentException("Impossible to find the behaviourid " + currentBehaviourId + " among the available ones, which are " + availableBehaviourIds);
        }
        this.currentBehaviourId = currentBehaviourId;
    }


    public MethodName getMethodName() {
        return methodName;
    }

    public BehaviourId getDefaultBehaviourId() {
        return defaultBehaviourId;
    }

    public BehaviourId getCurrentBehaviourId() {
        return currentBehaviourId;
    }

    public BehaviourIdSet getAvailableBehaviourIds() {
        return availableBehaviourIds;
    }

}
