package com.worksonourmachines.student.goal.mapper;

import org.openapitools.model.SharedStudentLearningGoal;
import org.springframework.stereotype.Component;

import com.worksonourmachines.student.goal.persistence.entity.LearningGoalEntity;
import com.worksonourmachines.student.goal.persistence.entity.LearningGoalLocation;

@Component
public class LearningGoalMapper {

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
