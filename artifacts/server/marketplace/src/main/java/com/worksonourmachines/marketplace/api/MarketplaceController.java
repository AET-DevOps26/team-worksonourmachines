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
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.marketplace.module.service.MarketplaceModuleService;
import com.worksonourmachines.marketplace.tutorapplication.service.MarketplaceTutorApplicationService;
import com.worksonourmachines.marketplace.tutorprofile.service.MarketplaceTutorProfileService;

import jakarta.validation.Valid;

@RestController
public class MarketplaceController implements MarketplaceApiV1 {

    private final MarketplaceModuleService marketplaceModuleService;
    private final MarketplaceTutorApplicationService marketplaceTutorApplicationService;
    private final MarketplaceTutorProfileService marketplaceTutorProfileService;

    public MarketplaceController(
            MarketplaceModuleService marketplaceModuleService,
            MarketplaceTutorApplicationService marketplaceTutorApplicationService,
            MarketplaceTutorProfileService marketplaceTutorProfileService) {
        this.marketplaceModuleService = marketplaceModuleService;
        this.marketplaceTutorApplicationService = marketplaceTutorApplicationService;
        this.marketplaceTutorProfileService = marketplaceTutorProfileService;
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
        return ResponseEntity.ok(marketplaceModuleService.getModule(code));
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorMeResponse> getMyTutorProfile() {
        return ResponseEntity.ok(marketplaceTutorProfileService.getMyTutorProfile());
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorDetail> getTutor(String id) {
        return ResponseEntity.ok(marketplaceTutorProfileService.getTutor(id));
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
        return ResponseEntity.ok(marketplaceModuleService.listModules(page, pageSize, q));
    }

    @Override
    public ResponseEntity<List<SharedMarketplaceTutorApplication>> listMyTutorApplications() {
        return ResponseEntity.ok(marketplaceTutorApplicationService.listMyTutorApplications());
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
            @Nullable Integer minRate,
            @Nullable Integer maxRate,
            @Nullable Float minRating,
            @Nullable List<SharedMarketplaceWeekday> weekdays,
            @Nullable SharedMarketplaceTutorSort sort) {
        return ResponseEntity.ok(marketplaceTutorProfileService.listTutors(
                page,
                pageSize,
                q,
                moduleId,
                topicId,
                languages,
                locations,
                minRate,
                maxRate,
                minRating,
                weekdays,
                sort));
    }

    @Override
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SharedMarketplaceTutorApplication> rejectTutorApplication(
            String id,
            @Valid @RequestBody SharedMarketplaceRejectApplicationRequest sharedMarketplaceRejectApplicationRequest) {
        return ResponseEntity.ok(marketplaceTutorApplicationService.rejectTutorApplication(
                id,
                sharedMarketplaceRejectApplicationRequest));
    }

    @Override
    public ResponseEntity<SharedMarketplaceTutorApplication> submitTutorApplication(
            @Valid @RequestBody SharedMarketplaceSubmitTutorApplicationRequest sharedMarketplaceSubmitTutorApplicationRequest) {
        return ResponseEntity.ok(marketplaceTutorApplicationService.submitTutorApplication(
                sharedMarketplaceSubmitTutorApplicationRequest));
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
            @Valid @RequestBody SharedMarketplaceTutorProfileInput sharedMarketplaceTutorProfileInput) {
        return ResponseEntity.ok(marketplaceTutorProfileService.updateMyTutorProfile(sharedMarketplaceTutorProfileInput));
    }
}
