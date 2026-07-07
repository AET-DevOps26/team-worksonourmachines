package com.worksonourmachines.student;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.openapitools.api.StudentApiV1;
import org.openapitools.model.SharedStudentStudentProfile;
import org.openapitools.model.SharedStudentStudentProfileInput;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class StudentController implements StudentApiV1 {

    private final AtomicReference<SharedStudentStudentProfile> profile = new AtomicReference<>(defaultProfile());

    @Override
    public ResponseEntity<SharedStudentStudentProfile> getMyProfile() {
        return ResponseEntity.ok(profile.get());
    }

    @Override
    public ResponseEntity<SharedStudentStudentProfile> updateMyProfile(
            @Valid @RequestBody SharedStudentStudentProfileInput input) {
        SharedStudentStudentProfile updated = new SharedStudentStudentProfile(
                input.getDisplayName(),
                input.getBio(),
                input.getLanguages());
        updated.setStudyFocus(input.getStudyFocus());
        profile.set(updated);
        return ResponseEntity.ok(updated);
    }

    private static SharedStudentStudentProfile defaultProfile() {
        SharedStudyFocusStudyFocus studyFocus = new SharedStudyFocusStudyFocus(3, 3, 3, 3);
        SharedStudentStudentProfile profile = new SharedStudentStudentProfile(
                "Demo Student",
                "Temporary in-memory profile for Student service experiments.",
                List.of("English"));
        profile.setStudyFocus(studyFocus);
        return profile;
    }
}
