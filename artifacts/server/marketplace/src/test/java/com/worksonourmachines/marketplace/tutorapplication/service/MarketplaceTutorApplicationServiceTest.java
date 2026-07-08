package com.worksonourmachines.marketplace.tutorapplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceApproveApplicationResponse;
import org.openapitools.model.SharedMarketplaceRejectApplicationRequest;
import org.openapitools.model.SharedMarketplaceSubmitTutorApplicationRequest;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
import com.worksonourmachines.marketplace.module.persistence.repository.MarketplaceModuleRepository;
import com.worksonourmachines.marketplace.tutorapplication.mapper.MarketplaceTutorApplicationMapper;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationEntity;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationStatus;
import com.worksonourmachines.marketplace.tutorapplication.persistence.repository.MarketplaceTutorApplicationRepository;
import com.worksonourmachines.server.common.security.AuthenticatedUser;

class MarketplaceTutorApplicationServiceTest {

    private final AuthenticatedUser authenticatedUser = org.mockito.Mockito.mock(AuthenticatedUser.class);
    private final MarketplaceModuleRepository moduleRepository = org.mockito.Mockito.mock(MarketplaceModuleRepository.class);
    private final MarketplaceTutorApplicationRepository repository = org.mockito.Mockito.mock(
            MarketplaceTutorApplicationRepository.class);
    private final MarketplaceTutorApplicationService service = new MarketplaceTutorApplicationService(
            authenticatedUser,
            moduleRepository,
            repository,
            new MarketplaceTutorApplicationMapper());

    @Test
    void submitsTutorApplication() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111101");
        UUID moduleId = UUID.fromString("11111111-1111-1111-1111-111111111201");
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111301");
        MarketplaceModuleEntity module = module(moduleId);
        when(authenticatedUser.id()).thenReturn(userId);
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(repository.existsByUserIdAndModule_IdAndStatus(
                userId,
                moduleId,
                MarketplaceTutorApplicationStatus.PENDING)).thenReturn(false);
        when(repository.save(any(MarketplaceTutorApplicationEntity.class))).thenAnswer(invocation -> {
            MarketplaceTutorApplicationEntity application = invocation.getArgument(0);
            ReflectionTestUtils.setField(application, "id", applicationId);
            return application;
        });

        SharedMarketplaceTutorApplication response = service.submitTutorApplication(
                new SharedMarketplaceSubmitTutorApplicationRequest(
                        moduleId.toString(),
                        " cert://intro.pdf "));

        assertEquals(applicationId.toString(), response.getId());
        assertEquals(userId.toString(), response.getUserId());
        assertEquals(moduleId.toString(), response.getModuleId());
        assertEquals("IN0001", response.getModuleCode());
        assertEquals(SharedMarketplaceApplicationStatus.PENDING, response.getStatus());
        assertEquals("cert://intro.pdf", response.getCertificateRef());
    }

    @Test
    void rejectsTutorApplicationSubmissionForUnknownModule() {
        UUID moduleId = UUID.fromString("11111111-1111-1111-1111-111111111299");
        when(authenticatedUser.id()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111101"));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.submitTutorApplication(new SharedMarketplaceSubmitTutorApplicationRequest(
                        moduleId.toString(),
                        "cert://intro.pdf")));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void rejectsDuplicatePendingTutorApplicationSubmission() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111101");
        UUID moduleId = UUID.fromString("11111111-1111-1111-1111-111111111201");
        when(authenticatedUser.id()).thenReturn(userId);
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module(moduleId)));
        when(repository.existsByUserIdAndModule_IdAndStatus(
                userId,
                moduleId,
                MarketplaceTutorApplicationStatus.PENDING)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.submitTutorApplication(new SharedMarketplaceSubmitTutorApplicationRequest(
                        moduleId.toString(),
                        "cert://intro.pdf")));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void approvesTutorApplication() {
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111301");
        MarketplaceTutorApplicationEntity application = application(
                applicationId,
                MarketplaceTutorApplicationStatus.PENDING);
        application.setRejectionReason("Previous review note.");
        when(repository.findWithModuleById(applicationId)).thenReturn(Optional.of(application));
        when(repository.existsByUserIdAndStatus(
                application.getUserId(),
                MarketplaceTutorApplicationStatus.APPROVED)).thenReturn(false);
        when(repository.save(application)).thenReturn(application);

        SharedMarketplaceApproveApplicationResponse response = service.approveTutorApplication(applicationId.toString());

        assertTrue(response.getIsFirstApproval());
        assertEquals(SharedMarketplaceApplicationStatus.APPROVED, response.getApplication().getStatus());
        assertNull(response.getApplication().getRejectionReason());
        assertEquals(MarketplaceTutorApplicationStatus.APPROVED, application.getStatus());
        assertNull(application.getRejectionReason());
        verify(repository).save(application);
    }

    @Test
    void returnsFalseWhenTutorAlreadyHasApprovedApplication() {
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111302");
        MarketplaceTutorApplicationEntity application = application(
                applicationId,
                MarketplaceTutorApplicationStatus.PENDING);
        when(repository.findWithModuleById(applicationId)).thenReturn(Optional.of(application));
        when(repository.existsByUserIdAndStatus(
                application.getUserId(),
                MarketplaceTutorApplicationStatus.APPROVED)).thenReturn(true);
        when(repository.save(application)).thenReturn(application);

        SharedMarketplaceApproveApplicationResponse response = service.approveTutorApplication(applicationId.toString());

        assertFalse(response.getIsFirstApproval());
        assertEquals(SharedMarketplaceApplicationStatus.APPROVED, response.getApplication().getStatus());
    }

    @Test
    void returnsNotFoundForUnknownTutorApplication() {
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111399");
        when(repository.findWithModuleById(applicationId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.approveTutorApplication(applicationId.toString()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void rejectsTutorApplication() {
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111303");
        MarketplaceTutorApplicationEntity application = application(
                applicationId,
                MarketplaceTutorApplicationStatus.PENDING);
        when(repository.findWithModuleById(applicationId)).thenReturn(Optional.of(application));
        when(repository.save(application)).thenReturn(application);

        SharedMarketplaceTutorApplication response = service.rejectTutorApplication(
                applicationId.toString(),
                new SharedMarketplaceRejectApplicationRequest().reason("Certificate does not match the module."));

        assertEquals(SharedMarketplaceApplicationStatus.REJECTED, response.getStatus());
        assertEquals("Certificate does not match the module.", response.getRejectionReason());
        assertEquals(MarketplaceTutorApplicationStatus.REJECTED, application.getStatus());
        assertEquals("Certificate does not match the module.", application.getRejectionReason());
        verify(repository).save(application);
    }

    @Test
    void returnsNotFoundWhenRejectingUnknownTutorApplication() {
        UUID applicationId = UUID.fromString("11111111-1111-1111-1111-111111111398");
        when(repository.findWithModuleById(applicationId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.rejectTutorApplication(
                        applicationId.toString(),
                        new SharedMarketplaceRejectApplicationRequest()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private static MarketplaceTutorApplicationEntity application(
            UUID applicationId,
            MarketplaceTutorApplicationStatus status) {
        UUID moduleId = UUID.fromString("11111111-1111-1111-1111-111111111201");
        MarketplaceModuleEntity module = module(moduleId);

        MarketplaceTutorApplicationEntity application = new MarketplaceTutorApplicationEntity(
                UUID.fromString("11111111-1111-1111-1111-111111111101"),
                module,
                status,
                "certificates/in0001.pdf",
                OffsetDateTime.parse("2026-07-08T12:00:00Z"));
        ReflectionTestUtils.setField(application, "id", applicationId);
        return application;
    }

    private static MarketplaceModuleEntity module(UUID moduleId) {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Introduction to Informatics",
                "Foundations of computer science.",
                "Good for first-semester students.");
        ReflectionTestUtils.setField(module, "id", moduleId);
        return module;
    }
}
