package com.worksonourmachines.marketplace.tutorapplication.service;

import java.util.List;
import java.util.UUID;

import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceApproveApplicationResponse;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.tutorapplication.mapper.MarketplaceTutorApplicationMapper;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationStatus;
import com.worksonourmachines.marketplace.tutorapplication.persistence.repository.MarketplaceTutorApplicationRepository;

@Service
public class MarketplaceTutorApplicationService {

    private final MarketplaceTutorApplicationRepository marketplaceTutorApplicationRepository;
    private final MarketplaceTutorApplicationMapper marketplaceTutorApplicationMapper;

    public MarketplaceTutorApplicationService(
            MarketplaceTutorApplicationRepository marketplaceTutorApplicationRepository,
            MarketplaceTutorApplicationMapper marketplaceTutorApplicationMapper) {
        this.marketplaceTutorApplicationRepository = marketplaceTutorApplicationRepository;
        this.marketplaceTutorApplicationMapper = marketplaceTutorApplicationMapper;
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

    @Transactional
    public SharedMarketplaceApproveApplicationResponse approveTutorApplication(String id) {
        var application = marketplaceTutorApplicationRepository.findWithModuleById(parseApplicationId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor application not found."));
        boolean isFirstApproval = !marketplaceTutorApplicationRepository.existsByUserIdAndStatus(
                application.getUserId(),
                MarketplaceTutorApplicationStatus.APPROVED);

        application.setStatus(MarketplaceTutorApplicationStatus.APPROVED);
        application.setRejectionReason(null);

        return new SharedMarketplaceApproveApplicationResponse(
                marketplaceTutorApplicationMapper.toDto(marketplaceTutorApplicationRepository.save(application)),
                isFirstApproval);
    }

    private static UUID parseApplicationId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor application not found.", exception);
        }
    }
}
