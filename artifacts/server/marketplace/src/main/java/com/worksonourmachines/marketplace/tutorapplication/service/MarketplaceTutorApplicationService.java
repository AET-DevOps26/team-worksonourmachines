package com.worksonourmachines.marketplace.tutorapplication.service;

import java.util.List;

import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
