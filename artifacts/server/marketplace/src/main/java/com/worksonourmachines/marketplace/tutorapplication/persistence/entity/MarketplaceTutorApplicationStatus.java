package com.worksonourmachines.marketplace.tutorapplication.persistence.entity;

import org.openapitools.model.SharedMarketplaceApplicationStatus;

public enum MarketplaceTutorApplicationStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public SharedMarketplaceApplicationStatus toDto() {
        return SharedMarketplaceApplicationStatus.fromValue(name().toLowerCase());
    }

    public static MarketplaceTutorApplicationStatus fromDto(SharedMarketplaceApplicationStatus status) {
        return MarketplaceTutorApplicationStatus.valueOf(status.getValue().toUpperCase());
    }
}
