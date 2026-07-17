package com.worksonourmachines.marketplace.tutorprofile.mapper;

import java.util.List;
import java.util.UUID;

import org.openapitools.model.SharedMarketplaceTutorAvailability;
import org.openapitools.model.SharedMarketplaceTutorCoverage;
import org.openapitools.model.SharedMarketplaceTutorDetail;
import org.openapitools.model.SharedMarketplaceTutorProfile;
import org.openapitools.model.SharedMarketplaceTutorProfileInput;
import org.openapitools.model.SharedMarketplaceTutorSummary;
import org.openapitools.model.TutorPage;
import org.springframework.stereotype.Component;

import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorAvailabilityEmbeddable;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorCoverageEntity;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorLocation;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorProfileEntity;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorWeekday;

@Component
public class MarketplaceTutorProfileMapper {

    public MarketplaceTutorProfileEntity toCreateEntity(UUID userId, SharedMarketplaceTutorProfileInput input) {
        MarketplaceTutorProfileEntity profile = new MarketplaceTutorProfileEntity(
                userId,
                input.getDisplayName().trim(),
                input.getBio().trim(),
                input.getHourlyRate(),
                Boolean.TRUE.equals(input.getPublished()));
        updateCollections(profile, input);
        return profile;
    }

    public void updateEntity(MarketplaceTutorProfileEntity profile, SharedMarketplaceTutorProfileInput input) {
        profile.setDisplayName(input.getDisplayName().trim());
        profile.setBio(input.getBio().trim());
        profile.setHourlyRate(input.getHourlyRate());
        profile.setPublished(Boolean.TRUE.equals(input.getPublished()));
        updateCollections(profile, input);
    }

    public SharedMarketplaceTutorProfile toProfile(MarketplaceTutorProfileEntity profile) {
        SharedMarketplaceTutorProfile dto = new SharedMarketplaceTutorProfile(
                profile.getDisplayName(),
                profile.getBio(),
                List.copyOf(profile.getLanguages()),
                toDtoLocations(profile),
                profile.getHourlyRate(),
                toDtoAvailability(profile),
                profile.getId().toString(),
                profile.getUserId().toString());
        dto.setPublished(profile.getPublished());
        return dto;
    }

    public TutorPage toPage(List<MarketplaceTutorProfileEntity> profiles, int page, int pageSize, int total) {
        return new TutorPage(
                profiles.stream()
                        .map(this::toSummary)
                        .toList(),
                page,
                pageSize,
                total);
    }

    public SharedMarketplaceTutorSummary toSummary(MarketplaceTutorProfileEntity profile) {
        return new SharedMarketplaceTutorSummary(
                profile.getId().toString(),
                profile.getUserId().toString(),
                profile.getDisplayName(),
                profile.getHourlyRate(),
                List.copyOf(profile.getLanguages()),
                toDtoLocations(profile),
                toDtoCoverages(profile));
    }

    public SharedMarketplaceTutorDetail toDetail(MarketplaceTutorProfileEntity profile) {
        return new SharedMarketplaceTutorDetail(
                profile.getId().toString(),
                profile.getUserId().toString(),
                profile.getDisplayName(),
                profile.getHourlyRate(),
                List.copyOf(profile.getLanguages()),
                toDtoLocations(profile),
                toDtoCoverages(profile),
                profile.getBio(),
                toDtoAvailability(profile),
                profile.getPublished());
    }

    private void updateCollections(MarketplaceTutorProfileEntity profile, SharedMarketplaceTutorProfileInput input) {
        profile.replaceLanguages(input.getLanguages().stream()
                .map(String::trim)
                .filter(language -> !language.isBlank())
                .toList());
        profile.replaceLocations(input.getLocations().stream()
                .map(MarketplaceTutorLocation::fromDto)
                .toList());
        profile.replaceAvailability(input.getAvailability().stream()
                .map(availability -> new MarketplaceTutorAvailabilityEmbeddable(
                        MarketplaceTutorWeekday.fromDto(availability.getWeekday()),
                        availability.getAvailable(),
                        availability.getNote()))
                .toList());
    }

    private List<org.openapitools.model.SharedMarketplaceLocation> toDtoLocations(MarketplaceTutorProfileEntity profile) {
        return profile.getLocations().stream()
                .map(MarketplaceTutorLocation::toDto)
                .toList();
    }

    private List<SharedMarketplaceTutorAvailability> toDtoAvailability(MarketplaceTutorProfileEntity profile) {
        return profile.getAvailability().stream()
                .map(availability -> new SharedMarketplaceTutorAvailability(
                        availability.getWeekday().toDto(),
                        availability.getAvailable())
                        .note(availability.getNote()))
                .toList();
    }

    private List<SharedMarketplaceTutorCoverage> toDtoCoverages(MarketplaceTutorProfileEntity profile) {
        return profile.getCoverages().stream()
                .map(this::toDtoCoverage)
                .toList();
    }

    private SharedMarketplaceTutorCoverage toDtoCoverage(MarketplaceTutorCoverageEntity coverage) {
        var module = coverage.getModule();
        return new SharedMarketplaceTutorCoverage(
                module.getId().toString(),
                module.getCode(),
                module.getTitle(),
                coverage.getProficiencyLevel());
    }
}
