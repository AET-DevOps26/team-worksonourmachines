package com.worksonourmachines.marketplace.tutorapplication.mapper;

import java.util.List;

import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.stereotype.Component;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationEntity;

@Component
public class MarketplaceTutorApplicationMapper {

    public List<SharedMarketplaceTutorApplication> toDtos(List<MarketplaceTutorApplicationEntity> applications) {
        return applications.stream()
                .map(this::toDto)
                .toList();
    }

    public SharedMarketplaceTutorApplication toDto(MarketplaceTutorApplicationEntity application) {
        MarketplaceModuleEntity module = application.getModule();
        SharedMarketplaceTutorApplication dto = new SharedMarketplaceTutorApplication(
                application.getId().toString(),
                application.getUserId().toString(),
                module.getId().toString(),
                module.getCode(),
                module.getTitle(),
                application.getStatus().toDto(),
                application.getCertificateRef(),
                application.getSubmittedAt());
        dto.setRejectionReason(application.getRejectionReason());
        return dto;
    }
}
