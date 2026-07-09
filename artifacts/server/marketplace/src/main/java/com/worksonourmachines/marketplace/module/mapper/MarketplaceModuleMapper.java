package com.worksonourmachines.marketplace.module.mapper;

import java.util.List;
import java.util.stream.IntStream;

import org.openapitools.model.ModulePage;
import org.openapitools.model.SharedMarketplaceAdminModuleInput;
import org.openapitools.model.SharedMarketplaceAdminModuleUpdateInput;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.openapitools.model.SharedMarketplaceModuleListItem;
import org.openapitools.model.SharedMarketplaceTopic;
import org.openapitools.model.SharedMarketplaceTopicInput;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.data.domain.Page;
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

    public void updateEntity(MarketplaceModuleEntity module, SharedMarketplaceAdminModuleUpdateInput input) {
        module.setTitle(input.getTitle());
        module.setDescription(input.getDescription());
        module.setDifficultyHint(input.getDifficultyHint());
        updateTopics(module, input.getTopics());
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

    public ModulePage toPage(Page<MarketplaceModuleEntity> modules, int page, int pageSize) {
        return new ModulePage(
                modules.getContent().stream()
                        .map(this::toListItem)
                        .toList(),
                page,
                pageSize,
                Math.toIntExact(modules.getTotalElements()));
    }

    private SharedMarketplaceModuleListItem toListItem(MarketplaceModuleEntity module) {
        return new SharedMarketplaceModuleListItem(
                module.getId().toString(),
                module.getCode(),
                module.getTitle(),
                module.getDescription(),
                module.getDifficultyHint());
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

    private void updateTopics(MarketplaceModuleEntity module, List<SharedMarketplaceTopicInput> inputs) {
        List<MarketplaceModuleTopicEntity> topics = module.getTopics();

        for (int index = 0; index < inputs.size(); index++) {
            if (index < topics.size()) {
                updateTopicEntity(topics.get(index), index, inputs.get(index));
            } else {
                module.addTopic(toTopicEntity(index, inputs.get(index)));
            }
        }

        if (topics.size() > inputs.size()) {
            topics.subList(inputs.size(), topics.size()).clear();
        }
    }

    private void updateTopicEntity(
            MarketplaceModuleTopicEntity topic,
            int position,
            SharedMarketplaceTopicInput input) {
        SharedStudyFocusStudyFocus studyFocus = input.getStudyFocus();
        topic.setPosition(position);
        topic.setName(input.getName());
        topic.setDescription(input.getDescription());
        topic.setDifficultyHint(input.getDifficultyHint());
        topic.setMemorization(studyFocus.getMemorization());
        topic.setFormalReasoning(studyFocus.getFormalReasoning());
        topic.setConceptualUnderstanding(studyFocus.getConceptualUnderstanding());
        topic.setProblemSolving(studyFocus.getProblemSolving());
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
