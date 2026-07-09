package com.worksonourmachines.marketplace.tutorapplication.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceApproveApplicationResponse;
import org.openapitools.model.SharedMarketplaceRejectApplicationRequest;
import org.openapitools.model.SharedMarketplaceSubmitTutorApplicationRequest;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
import com.worksonourmachines.marketplace.module.persistence.repository.MarketplaceModuleRepository;
import com.worksonourmachines.marketplace.tutorapplication.mapper.MarketplaceTutorApplicationMapper;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationEntity;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationStatus;
import com.worksonourmachines.marketplace.tutorapplication.persistence.repository.MarketplaceTutorApplicationRepository;
import com.worksonourmachines.marketplace.tutorprofile.mapper.MarketplaceTutorProfileMapper;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorProfileEntity;
import com.worksonourmachines.marketplace.tutorprofile.persistence.repository.MarketplaceTutorProfileRepository;
import com.worksonourmachines.server.common.security.AuthenticatedUser;

@Service
public class MarketplaceTutorApplicationService {

    private final AuthenticatedUser authenticatedUser;
    private final MarketplaceModuleRepository marketplaceModuleRepository;
    private final MarketplaceTutorApplicationRepository marketplaceTutorApplicationRepository;
    private final MarketplaceTutorApplicationMapper marketplaceTutorApplicationMapper;
    private final MarketplaceTutorProfileRepository marketplaceTutorProfileRepository;
    private final MarketplaceTutorProfileMapper marketplaceTutorProfileMapper;

    public MarketplaceTutorApplicationService(
            AuthenticatedUser authenticatedUser,
            MarketplaceModuleRepository marketplaceModuleRepository,
            MarketplaceTutorApplicationRepository marketplaceTutorApplicationRepository,
            MarketplaceTutorApplicationMapper marketplaceTutorApplicationMapper,
            MarketplaceTutorProfileRepository marketplaceTutorProfileRepository,
            MarketplaceTutorProfileMapper marketplaceTutorProfileMapper) {
        this.authenticatedUser = authenticatedUser;
        this.marketplaceModuleRepository = marketplaceModuleRepository;
        this.marketplaceTutorApplicationRepository = marketplaceTutorApplicationRepository;
        this.marketplaceTutorApplicationMapper = marketplaceTutorApplicationMapper;
        this.marketplaceTutorProfileRepository = marketplaceTutorProfileRepository;
        this.marketplaceTutorProfileMapper = marketplaceTutorProfileMapper;
    }

    @Transactional(readOnly = true)
    public List<SharedMarketplaceTutorApplication> listAdminTutorApplications(
            @Nullable SharedMarketplaceApplicationStatus status) {
        if (status == null) {
            return marketplaceTutorApplicationMapper.toDtos(
                    marketplaceTutorApplicationRepository.findAllByOrderBySubmittedAtDesc());
        }
        return marketplaceTutorApplicationMapper.toDtos(
                marketplaceTutorApplicationRepository.findByStatusOrderBySubmittedAtDesc(
                        MarketplaceTutorApplicationStatus.fromDto(status)));
    }

    @Transactional(readOnly = true)
    public List<SharedMarketplaceTutorApplication> listMyTutorApplications() {
        return marketplaceTutorApplicationMapper.toDtos(
                marketplaceTutorApplicationRepository.findByUserIdOrderBySubmittedAtDesc(authenticatedUser.id()));
    }

    @Transactional
    public SharedMarketplaceTutorApplication submitTutorApplication(
            SharedMarketplaceSubmitTutorApplicationRequest request) {
        UUID userId = authenticatedUser.id();
        UUID moduleId = parseModuleId(request.getModuleId());
        MarketplaceModuleEntity module = marketplaceModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown module."));

        if (request.getCertificateRef().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate reference is required.");
        }
        if (marketplaceTutorApplicationRepository.existsByUserIdAndModule_IdAndStatus(
                userId,
                moduleId,
                MarketplaceTutorApplicationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application already pending.");
        }
        marketplaceTutorProfileRepository.findByUserId(userId)
                .orElseGet(() -> createTutorProfile(userId, request));

        MarketplaceTutorApplicationEntity application = new MarketplaceTutorApplicationEntity(
                userId,
                module,
                MarketplaceTutorApplicationStatus.PENDING,
                request.getCertificateRef().trim(),
                OffsetDateTime.now(ZoneOffset.UTC));

        return marketplaceTutorApplicationMapper.toDto(marketplaceTutorApplicationRepository.save(application));
    }

    @Transactional
    public SharedMarketplaceApproveApplicationResponse approveTutorApplication(String id) {
        var application = findApplication(id);
        boolean isFirstApproval = !marketplaceTutorApplicationRepository.existsByUserIdAndStatus(
                application.getUserId(),
                MarketplaceTutorApplicationStatus.APPROVED);

        application.setStatus(MarketplaceTutorApplicationStatus.APPROVED);
        application.setRejectionReason(null);
        marketplaceTutorProfileRepository.findByUserId(application.getUserId())
                .ifPresent(profile -> {
                    profile.addCoverage(application.getModule(), "advanced");
                    marketplaceTutorProfileRepository.save(profile);
                });

        return new SharedMarketplaceApproveApplicationResponse(
                marketplaceTutorApplicationMapper.toDto(marketplaceTutorApplicationRepository.save(application)),
                isFirstApproval);
    }

    @Transactional
    public SharedMarketplaceTutorApplication rejectTutorApplication(
            String id,
            SharedMarketplaceRejectApplicationRequest request) {
        var application = findApplication(id);

        application.setStatus(MarketplaceTutorApplicationStatus.REJECTED);
        application.setRejectionReason(request.getReason());

        return marketplaceTutorApplicationMapper.toDto(marketplaceTutorApplicationRepository.save(application));
    }

    private MarketplaceTutorProfileEntity createTutorProfile(
            UUID userId,
            SharedMarketplaceSubmitTutorApplicationRequest request) {
        if (request.getProfile() == null || request.getProfile().getDisplayName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile is required on first application.");
        }
        return marketplaceTutorProfileRepository.save(
                marketplaceTutorProfileMapper.toCreateEntity(userId, request.getProfile()));
    }

    private MarketplaceTutorApplicationEntity findApplication(String id) {
        return marketplaceTutorApplicationRepository.findWithModuleById(parseApplicationId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor application not found."));
    }

    private static UUID parseModuleId(String moduleId) {
        try {
            return UUID.fromString(moduleId);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid module id.", exception);
        }
    }

    private static UUID parseApplicationId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor application not found.", exception);
        }
    }
}
