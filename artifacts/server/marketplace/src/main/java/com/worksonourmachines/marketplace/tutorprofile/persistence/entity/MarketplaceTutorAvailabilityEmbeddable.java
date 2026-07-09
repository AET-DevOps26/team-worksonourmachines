package com.worksonourmachines.marketplace.tutorprofile.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class MarketplaceTutorAvailabilityEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false, length = 32)
    private MarketplaceTutorWeekday weekday;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    protected MarketplaceTutorAvailabilityEmbeddable() {
    }

    public MarketplaceTutorAvailabilityEmbeddable(
            MarketplaceTutorWeekday weekday,
            Boolean available,
            String note) {
        this.weekday = weekday;
        this.available = available;
        this.note = note;
    }

    public MarketplaceTutorWeekday getWeekday() {
        return weekday;
    }

    public Boolean getAvailable() {
        return available;
    }

    public String getNote() {
        return note;
    }
}
