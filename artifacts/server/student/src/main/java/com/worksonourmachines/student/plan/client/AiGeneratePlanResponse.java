package com.worksonourmachines.student.plan.client;

import java.time.OffsetDateTime;
import java.util.List;

public record AiGeneratePlanResponse(
        String learningGoalId,
        List<AiPlanSuggestion> suggestions) {

    public record AiPlanSuggestion(
            String tier,
            String description,
            int totalEstimatedCost,
            List<AiProposedTutor> proposedTutors,
            List<AiPlanMilestone> milestones) {}

    public record AiProposedTutor(
            String id,
            String displayName,
            int hourlyRate) {}

    public record AiPlanMilestone(
            String title,
            OffsetDateTime dueDate,
            String topicId,
            String tutorId,
            int estimatedCost) {}
}
