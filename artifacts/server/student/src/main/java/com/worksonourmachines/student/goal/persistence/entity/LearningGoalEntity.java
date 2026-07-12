package com.worksonourmachines.student.goal.persistence.entity;

import java.time.OffsetDateTime;
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
@Table(name = "learning_goals", schema = "student")
public class LearningGoalEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "module_id", nullable = false)
    private String moduleId;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "target_date", nullable = false)
    private OffsetDateTime targetDate;

    @Column(name = "self_assessed_level", nullable = false)
    private Integer selfAssessedLevel;

    @Column(name = "budget_eur")
    private Integer budgetEur;

    @ElementCollection
    @CollectionTable(
            name = "learning_goal_locations",
            schema = "student",
            joinColumns = @JoinColumn(name = "goal_id"))
    @OrderColumn(name = "position")
    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false, length = 32)
    private List<LearningGoalLocation> locations = new ArrayList<>();

    protected LearningGoalEntity() {
    }

    public LearningGoalEntity(
            UUID studentId,
            String moduleId,
            String description,
            OffsetDateTime targetDate,
            Integer selfAssessedLevel,
            Integer budgetEur,
            List<LearningGoalLocation> locations) {
        this.studentId = studentId;
        this.moduleId = moduleId;
        this.description = description;
        this.targetDate = targetDate;
        this.selfAssessedLevel = selfAssessedLevel;
        this.budgetEur = budgetEur;
        this.locations = new ArrayList<>(locations);
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

    public UUID getStudentId() {
        return studentId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getTargetDate() {
        return targetDate;
    }

    public Integer getSelfAssessedLevel() {
        return selfAssessedLevel;
    }

    public Integer getBudgetEur() {
        return budgetEur;
    }

    public List<LearningGoalLocation> getLocations() {
        return locations;
    }
}
