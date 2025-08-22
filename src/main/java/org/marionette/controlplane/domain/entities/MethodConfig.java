package org.marionette.controlplane.domain.entities;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.marionette.controlplane.domain.values.BehaviourId;
import org.marionette.controlplane.domain.values.BehaviourIdSet;

/***
 * Entity
 */
public class MethodConfig {

    private final BehaviourId defaultBehaviourId;
    private final BehaviourId currentBehaviourId;
    private final BehaviourIdSet availableBehaviourIds;
    
    
    private MethodConfig(BehaviourId defaultBehaviourId, BehaviourId currentBehaviourId,
            BehaviourIdSet availableBehaviourIds) {
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
                    "The list of available behaviour ids does not contain the default behaviour id");
        }
        if (!availableBehaviourIds.contains(currentBehaviourId)) {
            throw new IllegalArgumentException(
                    "The list of available behaviour ids does not contain the current behaviour id");
        }
    }

    public static MethodConfig of(String defaultBehaviourId, String currentBehaviourId,
            Collection<String> availableBehaviourIds) {

        BehaviourId defaultBehaviour = new BehaviourId(defaultBehaviourId);
        BehaviourId currentBehaviour = new BehaviourId(currentBehaviourId);
        BehaviourIdSet availableBehaviours = BehaviourIdSet.fromStringCollection(availableBehaviourIds);

        return new MethodConfig(defaultBehaviour, currentBehaviour, availableBehaviours);

    }

    public static MethodConfig copyOf(MethodConfig other) {

        requireNonNull(other, "Other cannot be null when copying a MethodConfig object");

        return new MethodConfig(other.getDefaultBehaviourId(), other.getCurrentBehaviourId(), other.getAvailableBehaviourIds());
    }


    public MethodConfig changeCurrentBehaviourId(BehaviourId newBehaviourId) {

        requireNonNull(newBehaviourId, "The newBehaviourId field cannot be null");

        if(!availableBehaviourIds.contains(newBehaviourId)) {
            throw new IllegalArgumentException("Impossible to find the behaviour id " + newBehaviourId + " among the available ones, which are " + availableBehaviourIds);
        }

        return new MethodConfig(defaultBehaviourId, newBehaviourId, availableBehaviourIds);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultBehaviourId == null) ? 0 : defaultBehaviourId.hashCode());
        result = prime * result + ((availableBehaviourIds == null) ? 0 : availableBehaviourIds.hashCode());
        result = prime * result + ((currentBehaviourId == null) ? 0 : currentBehaviourId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodConfig other = (MethodConfig) obj;
        if (defaultBehaviourId == null) {
            if (other.defaultBehaviourId != null)
                return false;
        } else if (!defaultBehaviourId.equals(other.defaultBehaviourId))
            return false;
        if (availableBehaviourIds == null) {
            if (other.availableBehaviourIds != null)
                return false;
        } else if (!availableBehaviourIds.equals(other.availableBehaviourIds))
            return false;
        if (currentBehaviourId == null) {
            if (other.currentBehaviourId != null)
                return false;
        } else if (!currentBehaviourId.equals(other.currentBehaviourId))
            return false;
        return true;
    }

   

}
