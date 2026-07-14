package com.worksonourmachines.student.internal;

import java.util.UUID;

import org.openapitools.model.SharedStudentLearningGoal;
import org.openapitools.model.SharedStudentStudentProfile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.student.goal.service.LearningGoalService;
import com.worksonourmachines.student.profile.service.StudentProfileService;

@RestController
@RequestMapping("/v1/internal/students")
@PreAuthorize("hasRole('service')")
public class InternalStudentController {

    private final StudentProfileService studentProfileService;
    private final LearningGoalService learningGoalService;

    public InternalStudentController(
            StudentProfileService studentProfileService,
            LearningGoalService learningGoalService) {
        this.studentProfileService = studentProfileService;
        this.learningGoalService = learningGoalService;
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<SharedStudentStudentProfile> getStudentProfile(
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(studentProfileService.getProfileById(studentId));
    }

    @GetMapping("/{studentId}/goals/{goalId}")
    public ResponseEntity<SharedStudentLearningGoal> getLearningGoal(
            @PathVariable UUID studentId, @PathVariable UUID goalId) {
        return ResponseEntity.ok(learningGoalService.getGoalByStudentId(goalId, studentId));
    }
}
