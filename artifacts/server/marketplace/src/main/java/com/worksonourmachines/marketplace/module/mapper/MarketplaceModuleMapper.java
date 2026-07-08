package com.worksonourmachines.marketplace.module.mapper;

import java.util.List;
import java.util.stream.IntStream;

import org.openapitools.model.SharedMarketplaceAdminModuleInput;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.openapitools.model.SharedMarketplaceTopic;
import org.openapitools.model.SharedMarketplaceTopicInput;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.stereotype.Component;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleTopicEntity;

@Component
public class MarketplaceModuleMapper {

    public MarketplaceModuleEntity toCreateEntity(SharedMarketplaceAdminModuleInput input) {
        MarketplaceModuleEntity module = new MarketplaceModuleEntity(
                input.getCode(),
                input.getTitle(),
                input.getDescription(),
                input.getDifficultyHint());
        module.replaceTopics(IntStream.range(0, input.getTopics().size())
                .mapToObj(index -> toTopicEntity(index, input.getTopics().get(index)))
                .toList());
        return module;
    }

    public SharedMarketplaceModuleDetail toDetail(MarketplaceModuleEntity module) {
        return new SharedMarketplaceModuleDetail(
                module.getId().toString(),
                module.getCode(),
                module.getTitle(),
                module.getDescription(),
                module.getDifficultyHint(),
                module.getTopics().stream()
                        .map(this::toTopic)
                        .toList());
    }

    public List<SharedMarketplaceModuleDetail> toDetails(List<MarketplaceModuleEntity> modules) {
        return modules.stream()
                .map(this::toDetail)
                .toList();
    }

    private MarketplaceModuleTopicEntity toTopicEntity(int position, SharedMarketplaceTopicInput input) {
        SharedStudyFocusStudyFocus studyFocus = input.getStudyFocus();
        return new MarketplaceModuleTopicEntity(
                position,
                input.getName(),
                input.getDescription(),
                input.getDifficultyHint(),
                studyFocus.getMemorization(),
                studyFocus.getFormalReasoning(),
                studyFocus.getConceptualUnderstanding(),
                studyFocus.getProblemSolving());
    }

    private SharedMarketplaceTopic toTopic(MarketplaceModuleTopicEntity topic) {
        return new SharedMarketplaceTopic(
                topic.getId().toString(),
                topic.getName(),
                topic.getDescription(),
                topic.getDifficultyHint(),
                new SharedStudyFocusStudyFocus(
                        topic.getMemorization(),
                        topic.getFormalReasoning(),
                        topic.getConceptualUnderstanding(),
                        topic.getProblemSolving()));
    }
}
