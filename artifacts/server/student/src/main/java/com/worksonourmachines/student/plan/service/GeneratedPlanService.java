package com.worksonourmachines.student.plan.service;

import java.util.UUID;

import org.openapitools.model.SharedStudentGeneratedPlan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.server.common.security.AuthenticatedUser;
import com.worksonourmachines.student.goal.persistence.repository.LearningGoalRepository;
import com.worksonourmachines.student.plan.client.AiServiceClient;
import com.worksonourmachines.student.plan.mapper.GeneratedPlanMapper;
import com.worksonourmachines.student.plan.persistence.repository.GeneratedPlanRepository;

@Service
public class GeneratedPlanService {

    private final AuthenticatedUser authenticatedUser;
    private final LearningGoalRepository learningGoalRepository;
    private final GeneratedPlanRepository generatedPlanRepository;
    private final GeneratedPlanMapper generatedPlanMapper;
    private final AiServiceClient aiServiceClient;

    public GeneratedPlanService(
            AuthenticatedUser authenticatedUser,
            LearningGoalRepository learningGoalRepository,
            GeneratedPlanRepository generatedPlanRepository,
            GeneratedPlanMapper generatedPlanMapper,
            AiServiceClient aiServiceClient) {
        this.authenticatedUser = authenticatedUser;
        this.learningGoalRepository = learningGoalRepository;
        this.generatedPlanRepository = generatedPlanRepository;
        this.generatedPlanMapper = generatedPlanMapper;
        this.aiServiceClient = aiServiceClient;
    }

    @Transactional
    public SharedStudentGeneratedPlan generateAndSavePlan(String id) {
        UUID goalId = parseGoalId(id);
        UUID studentId = authenticatedUser.id();
        learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var aiResponse = aiServiceClient.generatePlan(id, authenticatedUser.bearerToken());
        generatedPlanRepository.deleteByGoalId(goalId);
        var entity = generatedPlanRepository.save(generatedPlanMapper.toEntity(goalId, aiResponse));
        return generatedPlanMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SharedStudentGeneratedPlan getPlan(String id) {
        UUID goalId = parseGoalId(id);
        UUID studentId = authenticatedUser.id();
        learningGoalRepository.findByIdAndStudentId(goalId, studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return generatedPlanRepository.findByGoalId(goalId)
                .map(generatedPlanMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static UUID parseGoalId(String id) {
        try {
            UUID goalId = UUID.fromString(id);
            if (!goalId.toString().equalsIgnoreCase(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return goalId;
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
