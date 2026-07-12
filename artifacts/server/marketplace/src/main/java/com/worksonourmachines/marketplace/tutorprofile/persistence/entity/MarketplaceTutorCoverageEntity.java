package com.worksonourmachines.marketplace.tutorprofile.persistence.entity;

import java.util.UUID;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutor_coverages", schema = "marketplace")
public class MarketplaceTutorCoverageEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MarketplaceTutorProfileEntity profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private MarketplaceModuleEntity module;

    @Column(name = "proficiency_level", nullable = false, length = 64)
    private String proficiencyLevel;

    protected MarketplaceTutorCoverageEntity() {
    }

    public MarketplaceTutorCoverageEntity(MarketplaceModuleEntity module, String proficiencyLevel) {
        this.module = module;
        this.proficiencyLevel = proficiencyLevel;
    }

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public MarketplaceTutorProfileEntity getProfile() {
        return profile;
    }

    void setProfile(MarketplaceTutorProfileEntity profile) {
        this.profile = profile;
    }

    public MarketplaceModuleEntity getModule() {
        return module;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }
}
