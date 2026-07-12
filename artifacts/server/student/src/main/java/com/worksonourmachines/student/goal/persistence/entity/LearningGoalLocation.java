package com.worksonourmachines.student.goal.persistence.entity;

import org.openapitools.model.SharedMarketplaceLocation;

public enum LearningGoalLocation {
    ONLINE,
    GARCHING,
    MUNICH,
    WEIHENSTEPHAN,
    STRAUBING,
    OTTOBRUNN;

    public SharedMarketplaceLocation toDto() {
        return SharedMarketplaceLocation.fromValue(name().toLowerCase());
    }

    public static LearningGoalLocation fromDto(SharedMarketplaceLocation location) {
        return LearningGoalLocation.valueOf(location.name());
    }
}
