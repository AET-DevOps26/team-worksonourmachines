package com.worksonourmachines.marketplace.tutorapplication.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutor_applications", schema = "marketplace")
public class MarketplaceTutorApplicationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private MarketplaceModuleEntity module;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private MarketplaceTutorApplicationStatus status;

    @Column(name = "certificate_ref", nullable = false, length = 512)
    private String certificateRef;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    protected MarketplaceTutorApplicationEntity() {
    }

    public MarketplaceTutorApplicationEntity(
            UUID userId,
            MarketplaceModuleEntity module,
            MarketplaceTutorApplicationStatus status,
            String certificateRef,
            OffsetDateTime submittedAt) {
        this.userId = userId;
        this.module = module;
        this.status = status;
        this.certificateRef = certificateRef;
        this.submittedAt = submittedAt;
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

    public UUID getUserId() {
        return userId;
    }

    public MarketplaceModuleEntity getModule() {
        return module;
    }

    public MarketplaceTutorApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(MarketplaceTutorApplicationStatus status) {
        this.status = status;
    }

    public String getCertificateRef() {
        return certificateRef;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
