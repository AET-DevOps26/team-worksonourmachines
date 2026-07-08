package com.worksonourmachines.marketplace.module.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openapitools.model.SharedMarketplaceModuleDetail;

import com.worksonourmachines.marketplace.module.mapper.MarketplaceModuleMapper;

class MarketplaceModuleMapperTest {

    private final MarketplaceModuleMapper mapper = new MarketplaceModuleMapper();

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
}
