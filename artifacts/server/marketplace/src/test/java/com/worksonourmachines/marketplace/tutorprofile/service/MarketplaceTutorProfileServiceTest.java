package com.worksonourmachines.marketplace.tutorprofile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.TutorPage;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import com.worksonourmachines.marketplace.tutorapplication.mapper.MarketplaceTutorApplicationMapper;
import com.worksonourmachines.marketplace.tutorapplication.persistence.repository.MarketplaceTutorApplicationRepository;
import com.worksonourmachines.marketplace.tutorprofile.mapper.MarketplaceTutorProfileMapper;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorLocation;
import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorProfileEntity;
import com.worksonourmachines.marketplace.tutorprofile.persistence.repository.MarketplaceTutorProfileRepository;
import com.worksonourmachines.server.common.security.AuthenticatedUser;

class MarketplaceTutorProfileServiceTest {

    private final AuthenticatedUser authenticatedUser = org.mockito.Mockito.mock(AuthenticatedUser.class);
    private final MarketplaceTutorProfileRepository repository = org.mockito.Mockito.mock(MarketplaceTutorProfileRepository.class);
    private final MarketplaceTutorApplicationRepository applicationRepository = org.mockito.Mockito.mock(
            MarketplaceTutorApplicationRepository.class);
    private final MarketplaceTutorProfileService service = new MarketplaceTutorProfileService(
            authenticatedUser,
            repository,
            new MarketplaceTutorProfileMapper(),
            applicationRepository,
            new MarketplaceTutorApplicationMapper());

    @Test
    void listsTutorsWithDatabaseSideSpecificationPagingAndSorting() {
        MarketplaceTutorProfileEntity profile = profile();
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(
                new PageImpl<>(
                        List.of(profile),
                        PageRequest.of(1, 2),
                        5));

        TutorPage response = service.listTutors(
                2,
                2,
                "ada",
                null,
                null,
                List.of("English"),
                null,
                10.0f,
                50.0f,
                null,
                null,
                SharedMarketplaceTutorSort.RATE_DESC);

        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getPage());
        assertEquals(2, response.getPageSize());
        assertEquals(5, response.getTotal());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable requestedPage = pageableCaptor.getValue();
        assertEquals(1, requestedPage.getPageNumber());
        assertEquals(2, requestedPage.getPageSize());
        assertEquals(Sort.Direction.DESC, requestedPage.getSort().getOrderFor("hourlyRate").getDirection());
        assertEquals(Sort.Direction.ASC, requestedPage.getSort().getOrderFor("displayName").getDirection());
    }

    private static MarketplaceTutorProfileEntity profile() {
        MarketplaceTutorProfileEntity profile = new MarketplaceTutorProfileEntity(
                UUID.fromString("11111111-1111-1111-1111-111111111101"),
                "Ada Lovelace",
                "Tutor bio.",
                25.0f,
                true);
        ReflectionTestUtils.setField(profile, "id", UUID.fromString("11111111-1111-1111-1111-111111111401"));
        profile.replaceLanguages(List.of("English"));
        profile.replaceLocations(List.of(MarketplaceTutorLocation.ONLINE));
        return profile;
    }
}
