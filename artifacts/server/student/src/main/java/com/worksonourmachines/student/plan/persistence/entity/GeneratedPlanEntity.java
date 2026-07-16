package com.worksonourmachines.student.plan.persistence.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "generated_plans", schema = "student")
public class GeneratedPlanEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "goal_id", nullable = false)
    private UUID goalId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "plan_id", nullable = false)
    @OrderBy("position ASC")
    private List<PlanSuggestionEntity> suggestions = new ArrayList<>();

    protected GeneratedPlanEntity() {
    }

    public GeneratedPlanEntity(UUID goalId, OffsetDateTime createdAt, List<PlanSuggestionEntity> suggestions) {
        this.goalId = goalId;
        this.createdAt = createdAt;
        this.suggestions = new ArrayList<>(suggestions);
    }

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() { return id; }
    public UUID getGoalId() { return goalId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public List<PlanSuggestionEntity> getSuggestions() { return suggestions; }
}
