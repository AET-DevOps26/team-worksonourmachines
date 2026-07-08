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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.marketplace.module.service.MarketplaceModuleService;
import com.worksonourmachines.marketplace.tutorapplication.service.MarketplaceTutorApplicationService;

import jakarta.validation.Valid;

@RestController
public class MarketplaceController implements MarketplaceApiV1 {

    private final MarketplaceModuleService marketplaceModuleService;
    private final MarketplaceTutorApplicationService marketplaceTutorApplicationService;

    public MarketplaceController(
            MarketplaceModuleService marketplaceModuleService,
            MarketplaceTutorApplicationService marketplaceTutorApplicationService) {
        this.marketplaceModuleService = marketplaceModuleService;
        this.marketplaceTutorApplicationService = marketplaceTutorApplicationService;
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<SharedMarketplaceModuleDetail>> listAdminModules() {
        return ResponseEntity.ok(marketplaceModuleService.listAdminModules());
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SharedMarketplaceApproveApplicationResponse> approveTutorApplication(String id) {
        return ResponseEntity.ok(marketplaceTutorApplicationService.approveTutorApplication(id));
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
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<SharedMarketplaceTutorApplication>> listAdminTutorApplications(
            @Valid @RequestParam(value = "status", required = false) @Nullable SharedMarketplaceApplicationStatus status) {
        return ResponseEntity.ok(marketplaceTutorApplicationService.listAdminTutorApplications(status));
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
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SharedMarketplaceModuleDetail> updateAdminModule(
            String code,
            @Valid @RequestBody SharedMarketplaceAdminModuleUpdateInput sharedMarketplaceAdminModuleUpdateInput) {
        return ResponseEntity.ok(marketplaceModuleService.updateAdminModule(code, sharedMarketplaceAdminModuleUpdateInput));
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
