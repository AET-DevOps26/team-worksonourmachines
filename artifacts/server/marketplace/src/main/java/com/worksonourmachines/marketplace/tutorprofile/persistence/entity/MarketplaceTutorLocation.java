package com.worksonourmachines.marketplace.tutorprofile.persistence.entity;

import org.openapitools.model.SharedMarketplaceLocation;

public enum MarketplaceTutorLocation {
    ONLINE,
    GARCHING,
    MUNICH,
    WEIHENSTEPHAN,
    STAUBING,
    OTTOBRUN;

    public SharedMarketplaceLocation toDto() {
        return SharedMarketplaceLocation.fromValue(name().toLowerCase());
    }

    public static MarketplaceTutorLocation fromDto(SharedMarketplaceLocation location) {
        return MarketplaceTutorLocation.valueOf(location.name());
    }
}
