package com.worksonourmachines.student.goal.mapper;

import java.util.UUID;

import org.openapitools.model.SharedStudentLearningGoal;
import org.openapitools.model.SharedStudentLearningGoalInput;
import org.springframework.stereotype.Component;

import com.worksonourmachines.student.goal.persistence.entity.LearningGoalEntity;
import com.worksonourmachines.student.goal.persistence.entity.LearningGoalLocation;

@Component
public class LearningGoalMapper {

    public LearningGoalEntity toCreateEntity(UUID studentId, SharedStudentLearningGoalInput input) {
        return new LearningGoalEntity(
                studentId,
                input.getModuleId().trim(),
                input.getDescription().trim(),
                input.getTargetDate(),
                input.getSelfAssessedLevel(),
                input.getBudgetEur(),
                input.getLocations().stream()
                        .map(LearningGoalLocation::fromDto)
                        .toList());
    }

    public void updateEntity(LearningGoalEntity entity, SharedStudentLearningGoalInput input) {
        entity.setModuleId(input.getModuleId().trim());
        entity.setDescription(input.getDescription().trim());
        entity.setTargetDate(input.getTargetDate());
        entity.setSelfAssessedLevel(input.getSelfAssessedLevel());
        entity.setBudgetEur(input.getBudgetEur());
        entity.replaceLocations(input.getLocations().stream()
                .map(LearningGoalLocation::fromDto)
                .toList());
    }

    public SharedStudentLearningGoal toDto(LearningGoalEntity entity) {
        SharedStudentLearningGoal goal = new SharedStudentLearningGoal(
                entity.getId().toString(),
                entity.getModuleId(),
                entity.getDescription(),
                entity.getTargetDate(),
                entity.getSelfAssessedLevel(),
                entity.getLocations().stream()
                        .map(LearningGoalLocation::toDto)
                        .toList());
        goal.setBudgetEur(entity.getBudgetEur());
        return goal;
    }
}
