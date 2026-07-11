package com.worksonourmachines.marketplace.tutorprofile.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.openapitools.model.SharedMarketplaceTutorDetail;
import org.openapitools.model.SharedMarketplaceTutorMeResponse;
import org.openapitools.model.SharedMarketplaceTutorProfile;
import org.openapitools.model.SharedMarketplaceTutorProfileInput;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.SharedMarketplaceWeekday;
import org.openapitools.model.TutorPage;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.tutorapplication.mapper.MarketplaceTutorApplicationMapper;
import com.worksonourmachines.marketplace.tutorapplication.persistence.repository.MarketplaceTutorApplicationRepository;
import com.worksonourmachines.marketplace.tutorprofile.mapper.MarketplaceTutorProfileMapper;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorLocation;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorProfileEntity;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorWeekday;
import com.worksonourmachines.marketplace.tutorprofile.persistence.repository.MarketplaceTutorProfileRepository;
import com.worksonourmachines.server.common.security.AuthenticatedUser;

@Service
public class MarketplaceTutorProfileService {

    private final AuthenticatedUser authenticatedUser;
    private final MarketplaceTutorProfileRepository marketplaceTutorProfileRepository;
    private final MarketplaceTutorProfileMapper marketplaceTutorProfileMapper;
    private final MarketplaceTutorApplicationRepository marketplaceTutorApplicationRepository;
    private final MarketplaceTutorApplicationMapper marketplaceTutorApplicationMapper;

    public MarketplaceTutorProfileService(
            AuthenticatedUser authenticatedUser,
            MarketplaceTutorProfileRepository marketplaceTutorProfileRepository,
            MarketplaceTutorProfileMapper marketplaceTutorProfileMapper,
            MarketplaceTutorApplicationRepository marketplaceTutorApplicationRepository,
            MarketplaceTutorApplicationMapper marketplaceTutorApplicationMapper) {
        this.authenticatedUser = authenticatedUser;
        this.marketplaceTutorProfileRepository = marketplaceTutorProfileRepository;
        this.marketplaceTutorProfileMapper = marketplaceTutorProfileMapper;
        this.marketplaceTutorApplicationRepository = marketplaceTutorApplicationRepository;
        this.marketplaceTutorApplicationMapper = marketplaceTutorApplicationMapper;
    }

    @Transactional(readOnly = true)
    public TutorPage listTutors(
            Integer page,
            Integer pageSize,
            @Nullable String q,
            @Nullable String moduleId,
            @Nullable String topicId,
            @Nullable List<String> languages,
            @Nullable List<SharedMarketplaceLocation> locations,
            @Nullable Float minRate,
            @Nullable Float maxRate,
            @Nullable Float minRating,
            @Nullable List<SharedMarketplaceWeekday> weekdays,
            @Nullable SharedMarketplaceTutorSort sort) {
        int resolvedPage = page == null ? 1 : Math.max(1, page);
        int resolvedPageSize = pageSize == null ? 20 : Math.max(1, Math.min(pageSize, 100));

        List<MarketplaceTutorProfileEntity> filtered = marketplaceTutorProfileRepository
                .findByPublishedTrueOrderByDisplayNameAsc()
                .stream()
                .filter(profile -> matchesFilters(
                        profile,
                        q,
                        moduleId,
                        topicId,
                        languages,
                        locations,
                        minRate,
                        maxRate,
                        minRating,
                        weekdays))
                .sorted(comparator(sort))
                .toList();

        int from = Math.min((resolvedPage - 1) * resolvedPageSize, filtered.size());
        int to = Math.min(from + resolvedPageSize, filtered.size());
        return marketplaceTutorProfileMapper.toPage(
                filtered.subList(from, to),
                resolvedPage,
                resolvedPageSize,
                filtered.size());
    }

    @Transactional(readOnly = true)
    public SharedMarketplaceTutorDetail getTutor(String id) {
        return marketplaceTutorProfileMapper.toDetail(
                marketplaceTutorProfileRepository.findByIdAndPublishedTrue(parseTutorId(id))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found.")));
    }

    @Transactional(readOnly = true)
    public SharedMarketplaceTutorMeResponse getMyTutorProfile() {
        UUID userId = authenticatedUser.id();
        SharedMarketplaceTutorProfile profile = marketplaceTutorProfileRepository.findByUserId(userId)
                .map(marketplaceTutorProfileMapper::toProfile)
                .orElse(null);
        List<SharedMarketplaceTutorApplication> applications = marketplaceTutorApplicationMapper.toDtos(
                marketplaceTutorApplicationRepository.findByUserIdOrderBySubmittedAtDesc(userId));
        return new SharedMarketplaceTutorMeResponse(profile, applications);
    }

    @Transactional
    public SharedMarketplaceTutorProfile updateMyTutorProfile(SharedMarketplaceTutorProfileInput input) {
        validateProfileInput(input);
        var profile = marketplaceTutorProfileRepository.findByUserId(authenticatedUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutor profile does not exist."));
        marketplaceTutorProfileMapper.updateEntity(profile, input);
        return marketplaceTutorProfileMapper.toProfile(marketplaceTutorProfileRepository.save(profile));
    }

    private boolean matchesFilters(
            MarketplaceTutorProfileEntity profile,
            @Nullable String q,
            @Nullable String moduleId,
            @Nullable String topicId,
            @Nullable List<String> languages,
            @Nullable List<SharedMarketplaceLocation> locations,
            @Nullable Float minRate,
            @Nullable Float maxRate,
            @Nullable Float minRating,
            @Nullable List<SharedMarketplaceWeekday> weekdays) {
        if (moduleId != null && profile.getCoverages().stream()
                .noneMatch(coverage -> coverage.getModule().getId().toString().equals(moduleId))) {
            return false;
        }
        if (topicId != null && profile.getCoverages().stream()
                .flatMap(coverage -> coverage.getModule().getTopics().stream())
                .noneMatch(topic -> topic.getId().toString().equals(topicId))) {
            return false;
        }
        if (languages != null && !languages.isEmpty() && !containsAllLanguages(profile, languages)) {
            return false;
        }
        if (locations != null && !locations.isEmpty() && locations.stream()
                .map(MarketplaceTutorLocation::fromDto)
                .noneMatch(profile.getLocations()::contains)) {
            return false;
        }
        if (minRate != null && profile.getHourlyRate() < minRate) {
            return false;
        }
        if (maxRate != null && profile.getHourlyRate() > maxRate) {
            return false;
        }
        if (minRating != null && 4.7f < minRating) {
            return false;
        }
        if (weekdays != null && !weekdays.isEmpty() && weekdays.stream()
                .map(MarketplaceTutorWeekday::fromDto)
                .noneMatch(weekday -> profile.getAvailability().stream()
                        .anyMatch(availability -> availability.getWeekday() == weekday
                                && Boolean.TRUE.equals(availability.getAvailable())))) {
            return false;
        }
        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase(Locale.ROOT).trim();
            String haystack = (
                    profile.getDisplayName()
                            + " "
                            + profile.getBio()
                            + " "
                            + profile.getCoverages().stream()
                                    .map(coverage -> coverage.getModule().getCode())
                                    .reduce("", (left, right) -> left + " " + right))
                    .toLowerCase(Locale.ROOT);
            return haystack.contains(needle);
        }
        return true;
    }

    private boolean containsAllLanguages(MarketplaceTutorProfileEntity profile, List<String> languages) {
        List<String> profileLanguages = profile.getLanguages().stream()
                .map(language -> language.toLowerCase(Locale.ROOT))
                .toList();
        return languages.stream()
                .map(language -> language.toLowerCase(Locale.ROOT))
                .allMatch(profileLanguages::contains);
    }

    private Comparator<MarketplaceTutorProfileEntity> comparator(@Nullable SharedMarketplaceTutorSort sort) {
        if (sort == SharedMarketplaceTutorSort.RATE_ASC) {
            return Comparator.comparing(MarketplaceTutorProfileEntity::getHourlyRate)
                    .thenComparing(MarketplaceTutorProfileEntity::getDisplayName);
        }
        if (sort == SharedMarketplaceTutorSort.RATE_DESC) {
            return Comparator.comparing(MarketplaceTutorProfileEntity::getHourlyRate)
                    .reversed()
                    .thenComparing(MarketplaceTutorProfileEntity::getDisplayName);
        }
        return Comparator.comparing(MarketplaceTutorProfileEntity::getDisplayName);
    }

    private static void validateProfileInput(SharedMarketplaceTutorProfileInput input) {
        if (input.getDisplayName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display name is required.");
        }
    }

    private static UUID parseTutorId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found.", exception);
        }
    }
}
