package com.worksonourmachines.student.plan.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan_suggestions", schema = "student")
public class PlanSuggestionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "position", nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 32)
    private PlanTier tier;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "total_estimated_cost", nullable = false)
    private int totalEstimatedCost;

    @ElementCollection
    @CollectionTable(
            name = "plan_suggestion_tutors",
            schema = "student",
            joinColumns = @JoinColumn(name = "suggestion_id"))
    @OrderColumn(name = "position")
    private List<PlanSuggestionTutor> proposedTutors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "plan_suggestion_milestones",
            schema = "student",
            joinColumns = @JoinColumn(name = "suggestion_id"))
    @OrderColumn(name = "position")
    private List<PlanSuggestionMilestone> milestones = new ArrayList<>();

    protected PlanSuggestionEntity() {
    }

    public PlanSuggestionEntity(
            UUID planId,
            int position,
            PlanTier tier,
            String description,
            int totalEstimatedCost,
            List<PlanSuggestionTutor> proposedTutors,
            List<PlanSuggestionMilestone> milestones) {
        this.planId = planId;
        this.position = position;
        this.tier = tier;
        this.description = description;
        this.totalEstimatedCost = totalEstimatedCost;
        this.proposedTutors = new ArrayList<>(proposedTutors);
        this.milestones = new ArrayList<>(milestones);
    }

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() { return id; }
    public UUID getPlanId() { return planId; }
    public int getPosition() { return position; }
    public PlanTier getTier() { return tier; }
    public String getDescription() { return description; }
    public int getTotalEstimatedCost() { return totalEstimatedCost; }
    public List<PlanSuggestionTutor> getProposedTutors() { return proposedTutors; }
    public List<PlanSuggestionMilestone> getMilestones() { return milestones; }
}
