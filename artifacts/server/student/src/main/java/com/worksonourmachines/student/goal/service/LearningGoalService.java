package com.worksonourmachines.student.goal.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.openapitools.model.SharedStudentLearningGoal;
import org.openapitools.model.SharedStudentLearningGoalInput;
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
    public SharedStudentLearningGoal getGoalByStudentId(UUID goalId, UUID studentId) {
        return learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .map(learningGoalMapper::toDto)
                .orElseThrow(LearningGoalService::notFound);
    }

    @Transactional(readOnly = true)
    public SharedStudentLearningGoal getGoal(String id) {
        UUID goalId = parseGoalId(id);
        UUID studentId = authenticatedUser.id();
        return learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .map(learningGoalMapper::toDto)
                .orElseThrow(LearningGoalService::notFound);
    }

    @Transactional(readOnly = true)
    public List<SharedStudentLearningGoal> listMyGoals() {
        UUID studentId = authenticatedUser.id();
        return learningGoalRepository.findAllByStudentIdOrderByTargetDateAscIdAsc(studentId).stream()
                .map(learningGoalMapper::toDto)
                .toList();
    }

    @Transactional
    public SharedStudentLearningGoal createGoal(SharedStudentLearningGoalInput input) {
        validateInput(input);
        UUID studentId = authenticatedUser.id();
        return learningGoalMapper.toDto(learningGoalRepository.save(
                learningGoalMapper.toCreateEntity(studentId, input)));
    }

    @Transactional
    public SharedStudentLearningGoal updateGoal(String id, SharedStudentLearningGoalInput input) {
        validateInput(input);
        UUID goalId = parseGoalId(id);
        UUID studentId = authenticatedUser.id();
        var goal = learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .orElseThrow(LearningGoalService::notFound);
        learningGoalMapper.updateEntity(goal, input);
        return learningGoalMapper.toDto(learningGoalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(String id) {
        UUID goalId = parseGoalId(id);
        UUID studentId = authenticatedUser.id();
        var goal = learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .orElseThrow(LearningGoalService::notFound);
        learningGoalRepository.delete(goal);
    }

    private static void validateInput(SharedStudentLearningGoalInput input) {
        if (input == null
                || isBlank(input.getModuleId())
                || isBlank(input.getDescription())
                || input.getTargetDate() == null
                || input.getSelfAssessedLevel() == null
                || input.getSelfAssessedLevel() < 1
                || input.getSelfAssessedLevel() > 5
                || (input.getBudgetEur() != null && input.getBudgetEur() < 0)
                || input.getLocations() == null
                || input.getLocations().stream().anyMatch(Objects::isNull)) {
            throw badRequest();
        }
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

    private static ResponseStatusException badRequest() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
