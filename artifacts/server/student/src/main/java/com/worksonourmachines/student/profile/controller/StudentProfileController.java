package com.worksonourmachines.student.profile.controller;

import org.openapitools.api.StudentApiV1;
import org.openapitools.model.SharedStudentLearningGoal;
import org.openapitools.model.SharedStudentLearningGoalInput;
import org.openapitools.model.SharedStudentStudentProfile;
import org.openapitools.model.SharedStudentStudentProfileInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.student.goal.service.LearningGoalService;
import com.worksonourmachines.student.profile.service.StudentProfileService;

import java.util.List;

@RestController
public class StudentProfileController implements StudentApiV1 {

    private final StudentProfileService studentProfileService;
    private final LearningGoalService learningGoalService;

    public StudentProfileController(
            StudentProfileService studentProfileService,
            LearningGoalService learningGoalService) {
        this.studentProfileService = studentProfileService;
        this.learningGoalService = learningGoalService;
    }

    @Override
    public ResponseEntity<SharedStudentLearningGoal> createGoal(SharedStudentLearningGoalInput sharedStudentLearningGoalInput) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningGoalService.createGoal(sharedStudentLearningGoalInput));
    }

    @Override
    public ResponseEntity<Void> deleteGoal(String id) {
        return null;
    }

    @Override
    public ResponseEntity<SharedStudentLearningGoal> getGoal(String id) {
        return ResponseEntity.ok(learningGoalService.getGoal(id));
    }

    @Override
    public ResponseEntity<SharedStudentStudentProfile> getMyProfile() {
        return ResponseEntity.ok(studentProfileService.getCurrentStudentProfile());
    }

    @Override
    public ResponseEntity<List<SharedStudentLearningGoal>> listMyGoals() {
        return ResponseEntity.ok(learningGoalService.listMyGoals());
    }

    @Override
    public ResponseEntity<SharedStudentLearningGoal> updateGoal(String id, SharedStudentLearningGoalInput sharedStudentLearningGoalInput) {
        return null;
    }

    @Override
    public ResponseEntity<SharedStudentStudentProfile> updateMyProfile(SharedStudentStudentProfileInput input) {
        return ResponseEntity.ok(studentProfileService.updateCurrentStudentProfile(input));
    }
}
