package com.worksonourmachines.marketplace.tutorprofile.persistence.entity;

import org.openapitools.model.SharedMarketplaceWeekday;

public enum MarketplaceTutorWeekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public SharedMarketplaceWeekday toDto() {
        return SharedMarketplaceWeekday.fromValue(name().toLowerCase());
    }

    public static MarketplaceTutorWeekday fromDto(SharedMarketplaceWeekday weekday) {
        return MarketplaceTutorWeekday.valueOf(weekday.name());
    }
}
