package com.worksonourmachines.marketplace.module.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openapitools.model.ModulePage;
import org.openapitools.model.SharedMarketplaceAdminModuleInput;
import org.openapitools.model.SharedMarketplaceAdminModuleUpdateInput;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.openapitools.model.SharedMarketplaceTopicInput;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.data.domain.PageImpl;

import com.worksonourmachines.marketplace.module.mapper.MarketplaceModuleMapper;

class MarketplaceModuleMapperTest {

    private final MarketplaceModuleMapper mapper = new MarketplaceModuleMapper();

    @Test
    void updatesEntityFromAdminModuleUpdateInput() {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Old title",
                "Old description.",
                "Old hint.");
        module.addTopic(new MarketplaceModuleTopicEntity(
                0,
                "Old topic",
                "Old topic description.",
                "Old topic hint.",
                1,
                1,
                1,
                1));
        MarketplaceModuleTopicEntity existingTopic = module.getTopics().get(0);
        SharedMarketplaceAdminModuleUpdateInput input = new SharedMarketplaceAdminModuleUpdateInput(
                "New title",
                "New description.",
                "New hint.",
                List.of(new SharedMarketplaceTopicInput(
                        "New topic",
                        "New topic description.",
                        "New topic hint.",
                        new SharedStudyFocusStudyFocus(3, 4, 5, 2))));

        mapper.updateEntity(module, input);

        assertEquals("IN0001", module.getCode());
        assertEquals("New title", module.getTitle());
        assertEquals("New description.", module.getDescription());
        assertEquals("New hint.", module.getDifficultyHint());
        assertEquals(1, module.getTopics().size());
        assertSame(existingTopic, module.getTopics().get(0));
        assertEquals("New topic", module.getTopics().get(0).getName());
        assertEquals(0, module.getTopics().get(0).getPosition());
        assertEquals(5, module.getTopics().get(0).getConceptualUnderstanding());
    }

    @Test
    void updatesExistingTopicsBeforeAddingNewTailTopics() {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Old title",
                "Old description.",
                "Old hint.");
        module.addTopic(new MarketplaceModuleTopicEntity(
                0,
                "Old first topic",
                "Old first topic description.",
                "Old first topic hint.",
                1,
                1,
                1,
                1));
        MarketplaceModuleTopicEntity existingTopic = module.getTopics().get(0);
        SharedMarketplaceAdminModuleUpdateInput input = new SharedMarketplaceAdminModuleUpdateInput(
                "New title",
                "New description.",
                "New hint.",
                List.of(
                        new SharedMarketplaceTopicInput(
                                "Updated first topic",
                                "Updated first topic description.",
                                "Updated first topic hint.",
                                new SharedStudyFocusStudyFocus(2, 3, 4, 5)),
                        new SharedMarketplaceTopicInput(
                                "New second topic",
                                "New second topic description.",
                                "New second topic hint.",
                                new SharedStudyFocusStudyFocus(5, 4, 3, 2))));

        mapper.updateEntity(module, input);

        assertEquals(2, module.getTopics().size());
        assertSame(existingTopic, module.getTopics().get(0));
        assertEquals("Updated first topic", module.getTopics().get(0).getName());
        assertEquals(0, module.getTopics().get(0).getPosition());
        assertEquals("New second topic", module.getTopics().get(1).getName());
        assertEquals(1, module.getTopics().get(1).getPosition());
    }

    @Test
    void removesSurplusTailTopicsOnUpdate() {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Old title",
                "Old description.",
                "Old hint.");
        module.addTopic(new MarketplaceModuleTopicEntity(
                0,
                "Old first topic",
                "Old first topic description.",
                "Old first topic hint.",
                1,
                1,
                1,
                1));
        module.addTopic(new MarketplaceModuleTopicEntity(
                1,
                "Old second topic",
                "Old second topic description.",
                "Old second topic hint.",
                1,
                1,
                1,
                1));
        MarketplaceModuleTopicEntity existingTopic = module.getTopics().get(0);
        SharedMarketplaceAdminModuleUpdateInput input = new SharedMarketplaceAdminModuleUpdateInput(
                "New title",
                "New description.",
                "New hint.",
                List.of(new SharedMarketplaceTopicInput(
                        "Only topic",
                        "Only topic description.",
                        "Only topic hint.",
                        new SharedStudyFocusStudyFocus(3, 4, 5, 2))));

        mapper.updateEntity(module, input);

        assertEquals(1, module.getTopics().size());
        assertSame(existingTopic, module.getTopics().get(0));
        assertEquals("Only topic", module.getTopics().get(0).getName());
        assertEquals(0, module.getTopics().get(0).getPosition());
    }

    @Test
    void mapsAdminModuleInputToEntityWithTopicPositions() {
        SharedMarketplaceAdminModuleInput input = new SharedMarketplaceAdminModuleInput(
                "IN0001",
                "Introduction to Informatics",
                "Foundations of computer science.",
                "Good for first-semester students.",
                List.of(
                        new SharedMarketplaceTopicInput(
                                "Logic",
                                "Propositional and predicate logic.",
                                "Focus on formal reasoning.",
                                new SharedStudyFocusStudyFocus(2, 5, 4, 3)),
                        new SharedMarketplaceTopicInput(
                                "Algorithms",
                                "Basic algorithm design.",
                                "Focus on problem solving.",
                                new SharedStudyFocusStudyFocus(3, 4, 4, 5))));

        MarketplaceModuleEntity entity = mapper.toCreateEntity(input);

        assertEquals("IN0001", entity.getCode());
        assertEquals("Introduction to Informatics", entity.getTitle());
        assertEquals(2, entity.getTopics().size());
        assertEquals(0, entity.getTopics().get(0).getPosition());
        assertEquals(1, entity.getTopics().get(1).getPosition());
        assertEquals(5, entity.getTopics().get(1).getProblemSolving());
    }

    @Test
    void mapsModuleWithTopicsToGeneratedDto() {
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
        module.assignId();
        topic.assignId();

        SharedMarketplaceModuleDetail dto = mapper.toDetail(module);

        assertEquals(module.getId().toString(), dto.getId());
        assertEquals("IN0001", dto.getCode());
        assertEquals("Introduction to Informatics", dto.getTitle());
        assertEquals(1, dto.getTopics().size());
        assertEquals(List.of("Logic"), dto.getTopics().stream().map(topicDto -> topicDto.getName()).toList());
        assertEquals(5, dto.getTopics().get(0).getStudyFocus().getFormalReasoning());
    }

    @Test
    void mapsModulePageToGeneratedDto() {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                "IN0001",
                "Introduction to Informatics",
                "Foundations of computer science.",
                "Good for first-semester students.");
        module.assignId();

        ModulePage page = mapper.toPage(new PageImpl<>(List.of(module)), 1, 20);

        assertEquals(1, page.getPage());
        assertEquals(20, page.getPageSize());
        assertEquals(1, page.getTotal());
        assertEquals("IN0001", page.getItems().get(0).getCode());
        assertEquals("Introduction to Informatics", page.getItems().get(0).getTitle());
    }
}
