package com.worksonourmachines.student.plan.persistence.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PlanSuggestionMilestone {

    private String title;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @Column(name = "topic_id")
    private String topicId;

    @Column(name = "tutor_id")
    private String tutorId;

    @Column(name = "estimated_cost")
    private int estimatedCost;

    protected PlanSuggestionMilestone() {
    }

    public PlanSuggestionMilestone(String title, OffsetDateTime dueDate, String topicId, String tutorId, int estimatedCost) {
        this.title = title;
        this.dueDate = dueDate;
        this.topicId = topicId;
        this.tutorId = tutorId;
        this.estimatedCost = estimatedCost;
    }

    public String getTitle() { return title; }
    public OffsetDateTime getDueDate() { return dueDate; }
    public String getTopicId() { return topicId; }
    public String getTutorId() { return tutorId; }
    public int getEstimatedCost() { return estimatedCost; }
}
