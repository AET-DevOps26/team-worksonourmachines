package com.worksonourmachines.student.profile.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_profiles", schema = "student")
public class StudentProfileEntity {

    @Id
    @Column(name = "student_id", nullable = false, length = 128)
    private UUID studentId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "bio", nullable = false, columnDefinition = "text")
    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_profile_languages",
            schema = "student",
            joinColumns = @JoinColumn(name = "student_id"))
    @OrderColumn(name = "position")
    @Column(name = "language", nullable = false)
    private List<String> languages = new ArrayList<>();

    @Column(name = "memorization")
    private Integer memorization;

    @Column(name = "formal_reasoning")
    private Integer formalReasoning;

    @Column(name = "conceptual_understanding")
    private Integer conceptualUnderstanding;

    @Column(name = "problem_solving")
    private Integer problemSolving;

    protected StudentProfileEntity() {
    }

    public StudentProfileEntity(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
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

    public boolean hasStudyFocus() {
        return memorization != null
                && formalReasoning != null
                && conceptualUnderstanding != null
                && problemSolving != null;
    }

    public void clearStudyFocus() {
        memorization = null;
        formalReasoning = null;
        conceptualUnderstanding = null;
        problemSolving = null;
    }
}
