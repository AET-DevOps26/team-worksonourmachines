package com.worksonourmachines.student.plan.persistence.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PlanSuggestionTutor {

    private String tutorId;
    private String displayName;
    private int hourlyRate;

    protected PlanSuggestionTutor() {
    }

    public PlanSuggestionTutor(String tutorId, String displayName, int hourlyRate) {
        this.tutorId = tutorId;
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }

    public String getTutorId() { return tutorId; }
    public String getDisplayName() { return displayName; }
    public int getHourlyRate() { return hourlyRate; }
}
