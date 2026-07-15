package com.worksonourmachines.student.internal;

import java.util.UUID;

import org.openapitools.api.StudentInternalApiV1;
import org.openapitools.model.SharedStudentLearningGoal;
import org.openapitools.model.SharedStudentStudentProfile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.student.goal.service.LearningGoalService;
import com.worksonourmachines.student.profile.service.StudentProfileService;

@RestController
@PreAuthorize("hasRole('service')")
public class InternalStudentController implements StudentInternalApiV1 {

    private final StudentProfileService studentProfileService;
    private final LearningGoalService learningGoalService;

    public InternalStudentController(
            StudentProfileService studentProfileService,
            LearningGoalService learningGoalService) {
        this.studentProfileService = studentProfileService;
        this.learningGoalService = learningGoalService;
    }

    @Override
    public ResponseEntity<SharedStudentStudentProfile> getStudentProfile(String studentId) {
        return ResponseEntity.ok(studentProfileService.getProfileById(UUID.fromString(studentId)));
    }

    @Override
    public ResponseEntity<SharedStudentLearningGoal> getLearningGoal(String studentId, String goalId) {
        return ResponseEntity.ok(learningGoalService.getGoalByStudentId(UUID.fromString(goalId), UUID.fromString(studentId)));
    }
}
