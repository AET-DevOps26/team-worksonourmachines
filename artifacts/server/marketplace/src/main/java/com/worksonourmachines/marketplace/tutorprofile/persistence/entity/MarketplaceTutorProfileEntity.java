package com.worksonourmachines.marketplace.tutorprofile.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutor_profiles", schema = "marketplace")
public class MarketplaceTutorProfileEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "bio", nullable = false, columnDefinition = "text")
    private String bio;

    @Column(name = "hourly_rate", nullable = false)
    private Integer hourlyRate;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @ElementCollection
    @CollectionTable(
            name = "tutor_profile_languages",
            schema = "marketplace",
            joinColumns = @JoinColumn(name = "profile_id"))
    @OrderColumn(name = "position")
    @Column(name = "language", nullable = false, length = 128)
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "tutor_profile_locations",
            schema = "marketplace",
            joinColumns = @JoinColumn(name = "profile_id"))
    @OrderColumn(name = "position")
    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false, length = 32)
    private List<MarketplaceTutorLocation> locations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "tutor_profile_availability",
            schema = "marketplace",
            joinColumns = @JoinColumn(name = "profile_id"))
    @OrderColumn(name = "position")
    private List<MarketplaceTutorAvailabilityEmbeddable> availability = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketplaceTutorCoverageEntity> coverages = new ArrayList<>();

    protected MarketplaceTutorProfileEntity() {
    }

    public MarketplaceTutorProfileEntity(
            UUID userId,
            String displayName,
            String bio,
            Integer hourlyRate,
            Boolean published) {
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.hourlyRate = hourlyRate;
        this.published = published;
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

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void replaceLanguages(List<String> languages) {
        this.languages.clear();
        this.languages.addAll(languages);
    }

    public List<MarketplaceTutorLocation> getLocations() {
        return locations;
    }

    public void replaceLocations(List<MarketplaceTutorLocation> locations) {
        this.locations.clear();
        this.locations.addAll(locations);
    }

    public List<MarketplaceTutorAvailabilityEmbeddable> getAvailability() {
        return availability;
    }

    public void replaceAvailability(List<MarketplaceTutorAvailabilityEmbeddable> availability) {
        this.availability.clear();
        this.availability.addAll(availability);
    }

    public List<MarketplaceTutorCoverageEntity> getCoverages() {
        return coverages;
    }

    public void addCoverage(MarketplaceModuleEntity module, String proficiencyLevel) {
        boolean alreadyCovered = coverages.stream()
                .anyMatch(coverage -> coverage.getModule().getId().equals(module.getId()));
        if (alreadyCovered) {
            return;
        }
        MarketplaceTutorCoverageEntity coverage = new MarketplaceTutorCoverageEntity(module, proficiencyLevel);
        coverage.setProfile(this);
        coverages.add(coverage);
    }
}
