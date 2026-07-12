package com.worksonourmachines.student.goal.persistence.entity;

import org.openapitools.model.SharedMarketplaceLocation;

public enum LearningGoalLocation {
    ONLINE,
    GARCHING,
    MUNICH,
    WEIHENSTEPHAN,
    STAUBING,
    OTTOBRUN;

    public SharedMarketplaceLocation toDto() {
        return SharedMarketplaceLocation.fromValue(name().toLowerCase());
    }
}
