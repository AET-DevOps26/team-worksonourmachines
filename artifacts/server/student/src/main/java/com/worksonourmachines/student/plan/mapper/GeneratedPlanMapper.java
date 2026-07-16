package com.worksonourmachines.student.plan.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openapitools.model.SharedStudentGeneratedPlan;
import org.openapitools.model.SharedStudentGeneratedPlanMilestone;
import org.openapitools.model.SharedStudentGeneratedPlanSuggestion;
import org.openapitools.model.SharedStudentGeneratedPlanTutor;
import org.springframework.stereotype.Component;

import com.worksonourmachines.student.plan.client.AiGeneratePlanResponse;
import com.worksonourmachines.student.plan.client.AiGeneratePlanResponse.AiPlanSuggestion;
import com.worksonourmachines.student.plan.persistence.entity.GeneratedPlanEntity;
import com.worksonourmachines.student.plan.persistence.entity.PlanSuggestionEntity;
import com.worksonourmachines.student.plan.persistence.entity.PlanSuggestionMilestone;
import com.worksonourmachines.student.plan.persistence.entity.PlanSuggestionTutor;
import com.worksonourmachines.student.plan.persistence.entity.PlanTier;

@Component
public class GeneratedPlanMapper {

    public GeneratedPlanEntity toEntity(UUID goalId, AiGeneratePlanResponse response) {
        List<AiPlanSuggestion> rawSuggestions = response.suggestions();
        List<PlanSuggestionEntity> suggestions = new ArrayList<>();
        for (int i = 0; i < rawSuggestions.size(); i++) {
            AiPlanSuggestion s = rawSuggestions.get(i);
            List<PlanSuggestionTutor> tutors = s.proposedTutors().stream()
                    .map(t -> new PlanSuggestionTutor(t.id(), t.displayName(), t.hourlyRate()))
                    .toList();
            List<PlanSuggestionMilestone> milestones = s.milestones().stream()
                    .map(m -> new PlanSuggestionMilestone(
                            m.title(), m.dueDate(), m.topicId(), m.tutorId(), m.estimatedCost()))
                    .toList();
            suggestions.add(new PlanSuggestionEntity(
                    i,
                    PlanTier.valueOf(s.tier().toUpperCase().replace("-", "_")),
                    s.description(),
                    s.totalEstimatedCost(),
                    tutors,
                    milestones));
        }
        return new GeneratedPlanEntity(goalId, OffsetDateTime.now(), suggestions);
    }

    public SharedStudentGeneratedPlan toDto(GeneratedPlanEntity entity) {
        List<SharedStudentGeneratedPlanSuggestion> suggestions = entity.getSuggestions().stream()
                .map(s -> {
                    List<SharedStudentGeneratedPlanTutor> tutors = s.getProposedTutors().stream()
                            .map(t -> new SharedStudentGeneratedPlanTutor(
                                    t.getTutorId(), t.getDisplayName(), t.getHourlyRate()))
                            .toList();
                    List<SharedStudentGeneratedPlanMilestone> milestones = s.getMilestones().stream()
                            .map(m -> new SharedStudentGeneratedPlanMilestone(
                                    m.getTitle(), m.getDueDate(), m.getTopicId(),
                                    m.getTutorId(), m.getEstimatedCost()))
                            .toList();
                    return new SharedStudentGeneratedPlanSuggestion(
                            s.getTier().toDto(),
                            s.getDescription(),
                            s.getTotalEstimatedCost(),
                            tutors,
                            milestones);
                })
                .toList();
        return new SharedStudentGeneratedPlan(
                entity.getId().toString(),
                entity.getGoalId().toString(),
                entity.getCreatedAt(),
                suggestions);
    }
}
