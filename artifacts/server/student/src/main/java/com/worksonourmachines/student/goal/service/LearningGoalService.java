package com.worksonourmachines.student.goal.service;

import java.util.UUID;

import org.openapitools.model.SharedStudentLearningGoal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.server.common.security.AuthenticatedUser;
import com.worksonourmachines.student.goal.mapper.LearningGoalMapper;
import com.worksonourmachines.student.goal.persistence.repository.LearningGoalRepository;

@Service
public class LearningGoalService {

    private final AuthenticatedUser authenticatedUser;
    private final LearningGoalRepository learningGoalRepository;
    private final LearningGoalMapper learningGoalMapper;

    public LearningGoalService(
            AuthenticatedUser authenticatedUser,
            LearningGoalRepository learningGoalRepository,
            LearningGoalMapper learningGoalMapper) {
        this.authenticatedUser = authenticatedUser;
        this.learningGoalRepository = learningGoalRepository;
        this.learningGoalMapper = learningGoalMapper;
    }

    @Transactional(readOnly = true)
    public SharedStudentLearningGoal getGoal(String id) {
        UUID goalId = parseGoalId(id);
        UUID studentId = authenticatedUser.id();
        return learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .map(learningGoalMapper::toDto)
                .orElseThrow(LearningGoalService::notFound);
    }

    private static UUID parseGoalId(String id) {
        try {
            UUID goalId = UUID.fromString(id);
            if (!goalId.toString().equalsIgnoreCase(id)) {
                throw notFound();
            }
            return goalId;
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw notFound();
        }
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
