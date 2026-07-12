package com.worksonourmachines.marketplace.tutorprofile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.openapitools.model.SharedMarketplaceTutorDetail;
import org.openapitools.model.SharedMarketplaceTutorMeResponse;
import org.openapitools.model.SharedMarketplaceTutorProfile;
import org.openapitools.model.SharedMarketplaceTutorProfileInput;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.SharedMarketplaceWeekday;
import org.openapitools.model.TutorPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

        Page<MarketplaceTutorProfileEntity> tutorPage = marketplaceTutorProfileRepository.findAll(
                tutorSearchSpecification(
                        q,
                        moduleId,
                        topicId,
                        languages,
                        locations,
                        minRate,
                        maxRate,
                        minRating,
                        weekdays),
                PageRequest.of(resolvedPage - 1, resolvedPageSize, sort(sort)));

        return marketplaceTutorProfileMapper.toPage(
                tutorPage.getContent(),
                resolvedPage,
                resolvedPageSize,
                (int) tutorPage.getTotalElements());
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

    private static Specification<MarketplaceTutorProfileEntity> tutorSearchSpecification(
            @Nullable String q,
            @Nullable String moduleId,
            @Nullable String topicId,
            @Nullable List<String> languages,
            @Nullable List<SharedMarketplaceLocation> locations,
            @Nullable Float minRate,
            @Nullable Float maxRate,
            @Nullable Float minRating,
            @Nullable List<SharedMarketplaceWeekday> weekdays) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isTrue(root.get("published")));

            UUID parsedModuleId = parseOptionalUuid(moduleId);
            if (moduleId != null && parsedModuleId == null) {
                return criteriaBuilder.disjunction();
            }
            if (parsedModuleId != null) {
                var coverage = root.join("coverages");
                var module = coverage.join("module");
                predicates.add(criteriaBuilder.equal(module.get("id"), parsedModuleId));
            }

            UUID parsedTopicId = parseOptionalUuid(topicId);
            if (topicId != null && parsedTopicId == null) {
                return criteriaBuilder.disjunction();
            }
            if (parsedTopicId != null) {
                var coverage = root.join("coverages");
                var module = coverage.join("module");
                var topic = module.join("topics");
                predicates.add(criteriaBuilder.equal(topic.get("id"), parsedTopicId));
            }

            if (languages != null && !languages.isEmpty()) {
                languages.stream()
                        .map(language -> language.toLowerCase(Locale.ROOT).trim())
                        .filter(language -> !language.isBlank())
                        .forEach(language -> {
                            var profileLanguage = root.join("languages");
                            predicates.add(criteriaBuilder.equal(
                                    criteriaBuilder.lower(profileLanguage.as(String.class)),
                                    language));
                        });
            }

            if (locations != null && !locations.isEmpty()) {
                var profileLocation = root.join("locations");
                predicates.add(profileLocation.in(locations.stream()
                        .map(MarketplaceTutorLocation::fromDto)
                        .toList()));
            }

            if (minRate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("hourlyRate"), minRate));
            }
            if (maxRate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("hourlyRate"), maxRate));
            }
            if (minRating != null && 4.7f < minRating) {
//                TODO: add rating into TypeSpec & the filtering logic here
                return criteriaBuilder.conjunction();
            }

            if (weekdays != null && !weekdays.isEmpty()) {
                var availability = root.join("availability");
                predicates.add(criteriaBuilder.and(
                        availability.get("weekday").in(weekdays.stream()
                                .map(MarketplaceTutorWeekday::fromDto)
                                .toList()),
                        criteriaBuilder.isTrue(availability.get("available"))));
            }

            if (q != null && !q.isBlank()) {
                String needle = "%" + q.toLowerCase(Locale.ROOT).trim() + "%";
                var coverage = root.join("coverages", JoinType.LEFT);
                var module = coverage.join("module", JoinType.LEFT);
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("displayName")), needle),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("bio")), needle),
                        criteriaBuilder.like(criteriaBuilder.lower(module.get("code")), needle)));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Sort sort(@Nullable SharedMarketplaceTutorSort sort) {
        if (sort == SharedMarketplaceTutorSort.RATE_ASC) {
            return Sort.by(
                    Sort.Order.asc("hourlyRate"),
                    Sort.Order.asc("displayName"));
        }
        if (sort == SharedMarketplaceTutorSort.RATE_DESC) {
            return Sort.by(
                    Sort.Order.desc("hourlyRate"),
                    Sort.Order.asc("displayName"));
        }
        return Sort.by(Sort.Order.asc("displayName"));
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

    @Nullable
    private static UUID parseOptionalUuid(@Nullable String id) {
        if (id == null) {
            return null;
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
