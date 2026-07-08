package com.worksonourmachines.marketplace.tutorapplication.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.test.util.ReflectionTestUtils;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
import com.worksonourmachines.marketplace.tutorapplication.mapper.MarketplaceTutorApplicationMapper;

class MarketplaceTutorApplicationMapperTest {

    private final MarketplaceTutorApplicationMapper mapper = new MarketplaceTutorApplicationMapper();

    @Test
    void mapsTutorApplicationToGeneratedDto() {
        UUID moduleId = UUID.fromString("11111111-1111-1111-1111-111111111201");
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111301");
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111101");
        OffsetDateTime submittedAt = OffsetDateTime.parse("2026-07-08T12:00:00Z");

        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Introduction to Informatics",
                "Foundations of computer science.",
                "Good for first-semester students.");
        ReflectionTestUtils.setField(module, "id", moduleId);
        MarketplaceTutorApplicationEntity application = new MarketplaceTutorApplicationEntity(
                userId,
                module,
                MarketplaceTutorApplicationStatus.REJECTED,
                "certificates/in0001.pdf",
                submittedAt);
        ReflectionTestUtils.setField(application, "id", applicationId);
        application.setRejectionReason("Certificate was unreadable.");

        SharedMarketplaceTutorApplication dto = mapper.toDto(application);

        assertEquals(applicationId.toString(), dto.getId());
        assertEquals(userId.toString(), dto.getUserId());
        assertEquals(moduleId.toString(), dto.getModuleId());
        assertEquals("IN0001", dto.getModuleCode());
        assertEquals("Introduction to Informatics", dto.getModuleTitle());
        assertEquals(SharedMarketplaceApplicationStatus.REJECTED, dto.getStatus());
        assertEquals("certificates/in0001.pdf", dto.getCertificateRef());
        assertEquals(submittedAt, dto.getSubmittedAt());
        assertEquals("Certificate was unreadable.", dto.getRejectionReason());
    }
}
