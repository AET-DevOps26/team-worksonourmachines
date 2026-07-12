package com.worksonourmachines.marketplace.module.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.module.mapper.MarketplaceModuleMapper;
import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleTopicEntity;
import com.worksonourmachines.marketplace.module.persistence.repository.MarketplaceModuleRepository;

class MarketplaceModuleServiceTest {

    private final MarketplaceModuleRepository repository = org.mockito.Mockito.mock(MarketplaceModuleRepository.class);
    private final MarketplaceModuleService service = new MarketplaceModuleService(
            repository,
            new MarketplaceModuleMapper());

    @Test
    void getsModuleByCode() {
        MarketplaceModuleEntity module = module();
        when(repository.findByCodeIgnoreCase("IN0001")).thenReturn(Optional.of(module));

        SharedMarketplaceModuleDetail response = service.getModule("IN0001");

        assertEquals("IN0001", response.getCode());
        assertEquals("Introduction to Informatics", response.getTitle());
        assertEquals(1, response.getTopics().size());
        assertEquals("Logic", response.getTopics().get(0).getName());
    }

    @Test
    void returnsNotFoundForUnknownModuleCode() {
        when(repository.findByCodeIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getModule("UNKNOWN"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private static MarketplaceModuleEntity module() {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Introduction to Informatics",
                "Foundations of computer science.",
                "Good for first-semester students.");
        MarketplaceModuleTopicEntity topic = new MarketplaceModuleTopicEntity(
                0,
                "Logic",
                "Propositional and predicate logic.",
                "Focus on formal reasoning.",
                2,
                5,
                4,
                3);
        module.addTopic(topic);
        ReflectionTestUtils.setField(module, "id", UUID.fromString("11111111-1111-1111-1111-111111111201"));
        ReflectionTestUtils.setField(topic, "id", UUID.fromString("11111111-1111-1111-1111-111111111202"));
        return module;
    }
}
