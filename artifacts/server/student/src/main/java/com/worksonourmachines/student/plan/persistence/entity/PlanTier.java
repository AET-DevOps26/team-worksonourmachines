package com.worksonourmachines.student.plan.persistence.entity;

import org.openapitools.model.SharedStudentPlanTier;

public enum PlanTier {
    CHEAPEST,
    WITHIN_BUDGET,
    BEST_QUALITY;

    public SharedStudentPlanTier toDto() {
        return switch (this) {
            case CHEAPEST -> SharedStudentPlanTier.CHEAPEST;
            case WITHIN_BUDGET -> SharedStudentPlanTier.WITHIN_BUDGET;
            case BEST_QUALITY -> SharedStudentPlanTier.BEST_QUALITY;
        };
    }

    public static PlanTier fromDto(SharedStudentPlanTier tier) {
        return switch (tier) {
            case CHEAPEST -> CHEAPEST;
            case WITHIN_BUDGET -> WITHIN_BUDGET;
            case BEST_QUALITY -> BEST_QUALITY;
        };
    }
}
