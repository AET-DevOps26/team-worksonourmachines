package com.worksonourmachines.student.profile.controller;

import org.openapitools.api.StudentApiV1;
import org.openapitools.model.SharedStudentStudentProfile;
import org.openapitools.model.SharedStudentStudentProfileInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.worksonourmachines.student.profile.service.StudentProfileService;

import jakarta.validation.Valid;

@RestController
public class StudentProfileController implements StudentApiV1 {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @Override
    public ResponseEntity<SharedStudentStudentProfile> getMyProfile() {
        return ResponseEntity.ok(studentProfileService.getCurrentStudentProfile());
    }

    @Override
    public ResponseEntity<SharedStudentStudentProfile> updateMyProfile(
            @Valid @RequestBody SharedStudentStudentProfileInput input) {
        return ResponseEntity.ok(studentProfileService.updateCurrentStudentProfile(input));
    }
}
