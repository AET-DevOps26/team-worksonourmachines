package com.worksonourmachines.marketplace.module.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "module_topics", schema = "marketplace")
public class MarketplaceModuleTopicEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private MarketplaceModuleEntity module;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "difficulty_hint", nullable = false, columnDefinition = "text")
    private String difficultyHint;

    @Column(name = "memorization", nullable = false)
    private Integer memorization;

    @Column(name = "formal_reasoning", nullable = false)
    private Integer formalReasoning;

    @Column(name = "conceptual_understanding", nullable = false)
    private Integer conceptualUnderstanding;

    @Column(name = "problem_solving", nullable = false)
    private Integer problemSolving;

    protected MarketplaceModuleTopicEntity() {
    }

    public MarketplaceModuleTopicEntity(
            Integer position,
            String name,
            String description,
            String difficultyHint,
            Integer memorization,
            Integer formalReasoning,
            Integer conceptualUnderstanding,
            Integer problemSolving) {
        this.position = position;
        this.name = name;
        this.description = description;
        this.difficultyHint = difficultyHint;
        this.memorization = memorization;
        this.formalReasoning = formalReasoning;
        this.conceptualUnderstanding = conceptualUnderstanding;
        this.problemSolving = problemSolving;
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

    public MarketplaceModuleEntity getModule() {
        return module;
    }

    void setModule(MarketplaceModuleEntity module) {
        this.module = module;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficultyHint() {
        return difficultyHint;
    }

    public void setDifficultyHint(String difficultyHint) {
        this.difficultyHint = difficultyHint;
    }

    public Integer getMemorization() {
        return memorization;
    }

    public void setMemorization(Integer memorization) {
        this.memorization = memorization;
    }

    public Integer getFormalReasoning() {
        return formalReasoning;
    }

    public void setFormalReasoning(Integer formalReasoning) {
        this.formalReasoning = formalReasoning;
    }

    public Integer getConceptualUnderstanding() {
        return conceptualUnderstanding;
    }

    public void setConceptualUnderstanding(Integer conceptualUnderstanding) {
        this.conceptualUnderstanding = conceptualUnderstanding;
    }

    public Integer getProblemSolving() {
        return problemSolving;
    }

    public void setProblemSolving(Integer problemSolving) {
        this.problemSolving = problemSolving;
    }
}
