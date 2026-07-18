package com.worksonourmachines.marketplace.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.api.MarketplaceApiV1;
import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.SharedMarketplaceWeekday;
import org.openapitools.model.TutorPage;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worksonourmachines.marketplace.module.service.MarketplaceModuleService;
import com.worksonourmachines.marketplace.tutorapplication.service.MarketplaceTutorApplicationService;
import com.worksonourmachines.marketplace.tutorprofile.service.MarketplaceTutorProfileService;
import com.worksonourmachines.server.common.exception.ApiExceptionHandler;

class MarketplaceQueryParameterConversionTest {

    private final MarketplaceTutorApplicationService tutorApplicationService =
            mock(MarketplaceTutorApplicationService.class);
    private final MarketplaceTutorProfileService tutorProfileService = mock(MarketplaceTutorProfileService.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        new MarketplaceWebConfiguration().addFormatters(conversionService);

        MarketplaceController controller = new MarketplaceController(
                mock(MarketplaceModuleService.class),
                tutorApplicationService,
                tutorProfileService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setConversionService(conversionService)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void bindsLowercaseTutorQueryEnums() throws Exception {
        when(tutorProfileService.listTutors(
                        any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new TutorPage(List.of(), 1, 20, 0));

        mockMvc.perform(get(MarketplaceApiV1.PATH_LIST_TUTORS)
                        .queryParam("locations", "online")
                        .queryParam("weekdays", "monday")
                        .queryParam("sort", "rate_asc"))
                .andExpect(status().isOk());

        verify(tutorProfileService).listTutors(
                1,
                20,
                null,
                null,
                null,
                null,
                List.of(SharedMarketplaceLocation.ONLINE),
                null,
                null,
                List.of(SharedMarketplaceWeekday.MONDAY),
                SharedMarketplaceTutorSort.RATE_ASC);
    }

    @Test
    void bindsLowercaseApplicationStatus() throws Exception {
        when(tutorApplicationService.listAdminTutorApplications(any())).thenReturn(List.of());

        mockMvc.perform(get(MarketplaceApiV1.PATH_LIST_ADMIN_TUTOR_APPLICATIONS)
                        .queryParam("status", "pending"))
                .andExpect(status().isOk());

        verify(tutorApplicationService).listAdminTutorApplications(SharedMarketplaceApplicationStatus.PENDING);
    }

    @Test
    void invalidTutorSortReturnsStandardBadRequest() throws Exception {
        mockMvc.perform(get(MarketplaceApiV1.PATH_LIST_TUTORS)
                        .queryParam("sort", "unsupported"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request"))
                .andExpect(jsonPath("$.message")
                        .value("The server could not understand the request due to invalid syntax."));
    }
}
