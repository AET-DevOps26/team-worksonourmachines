package com.worksonourmachines.marketplace.api;

import java.util.List;

import org.openapitools.api.MarketplaceApiV1;
import org.openapitools.model.ModulePage;
import org.openapitools.model.SharedMarketplaceAdminModuleInput;
import org.openapitools.model.SharedMarketplaceAdminModuleUpdateInput;
import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceApproveApplicationResponse;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.openapitools.model.SharedMarketplaceRejectApplicationRequest;
import org.openapitools.model.SharedMarketplaceSubmitTutorApplicationRequest;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.openapitools.model.SharedMarketplaceTutorDetail;
import org.openapitools.model.SharedMarketplaceTutorMeResponse;
import org.openapitools.model.SharedMarketplaceTutorProfile;
import org.openapitools.model.SharedMarketplaceTutorProfileInput;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.SharedMarketplaceWeekday;
import org.openapitools.model.TutorPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.marketplace.module.service.MarketplaceModuleService;

import jakarta.validation.Valid;

@RestController
public class MarketplaceController implements MarketplaceApiV1 {

    private final MarketplaceModuleService marketplaceModuleService;

    public MarketplaceController(MarketplaceModuleService marketplaceModuleService) {
        this.marketplaceModuleService = marketplaceModuleService;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<SharedMarketplaceModuleDetail>> listAdminModules() {
        return ResponseEntity.ok(marketplaceModuleService.listAdminModules());
    }

    @Override
    public ResponseEntity<SharedMarketplaceApproveApplicationResponse> approveTutorApplication(String id) {
        return notImplemented();
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SharedMarketplaceModuleDetail> createAdminModule(
            @Valid @RequestBody SharedMarketplaceAdminModuleInput sharedMarketplaceAdminModuleInput) {
        return ResponseEntity.ok(marketplaceModuleService.createAdminModule(sharedMarketplaceAdminModuleInput));
    }

    @Override
    public ResponseEntity<SharedMarketplaceModuleDetail> getModule(String code) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorMeResponse> getMyTutorProfile() {
        return notImplemented();
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorDetail> getTutor(String id) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<List<SharedMarketplaceTutorApplication>> listAdminTutorApplications(
            @Nullable SharedMarketplaceApplicationStatus status) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<ModulePage> listModules(
            Integer page,
            Integer pageSize,
            @Nullable String q) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<List<SharedMarketplaceTutorApplication>> listMyTutorApplications() {
        return notImplemented();
    }

    @Override
    public ResponseEntity<TutorPage> listTutors(
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
        return notImplemented();
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorApplication> rejectTutorApplication(
            String id,
            SharedMarketplaceRejectApplicationRequest sharedMarketplaceRejectApplicationRequest) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorApplication> submitTutorApplication(
            SharedMarketplaceSubmitTutorApplicationRequest sharedMarketplaceSubmitTutorApplicationRequest) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<SharedMarketplaceModuleDetail> updateAdminModule(
            String code,
            SharedMarketplaceAdminModuleUpdateInput sharedMarketplaceAdminModuleUpdateInput) {
        return notImplemented();
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorProfile> updateMyTutorProfile(
            SharedMarketplaceTutorProfileInput sharedMarketplaceTutorProfileInput) {
        return notImplemented();
    }

    private static <T> ResponseEntity<T> notImplemented() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
