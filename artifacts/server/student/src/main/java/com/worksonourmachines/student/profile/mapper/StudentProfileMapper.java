package com.worksonourmachines.student.profile.mapper;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.openapitools.model.SharedStudentStudentProfile;
import org.openapitools.model.SharedStudentStudentProfileInput;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.stereotype.Component;

import com.worksonourmachines.student.profile.persistence.entity.StudentProfileEntity;

import javax.annotation.Nonnull;

@Component
public class StudentProfileMapper {

    public SharedStudentStudentProfile defaultDto(String name) {
        SharedStudentStudentProfile profile = new SharedStudentStudentProfile(
                name,
                "Add your bio here...",
                List.of("German"));
        profile.setStudyFocus(new SharedStudyFocusStudyFocus(0,0,0,0));
        return profile;
    }

    public SharedStudentStudentProfile toDto(StudentProfileEntity entity) {
        SharedStudentStudentProfile profile = new SharedStudentStudentProfile(
                entity.getDisplayName(),
                entity.getBio(),
                List.copyOf(entity.getLanguages()));
        if (entity.hasStudyFocus()) {
            profile.setStudyFocus(new SharedStudyFocusStudyFocus(
                    entity.getMemorization(),
                    entity.getFormalReasoning(),
                    entity.getConceptualUnderstanding(),
                    entity.getProblemSolving()));
        }
        return profile;
    }

    public void updateEntity(StudentProfileEntity entity, SharedStudentStudentProfileInput input) {
        entity.setDisplayName(input.getDisplayName());
        entity.setBio(input.getBio());
        entity.setLanguages(new ArrayList<>(input.getLanguages()));

        SharedStudyFocusStudyFocus studyFocus = input.getStudyFocus();
        if (studyFocus == null) {
            entity.clearStudyFocus();
            return;
        }
        entity.setMemorization(studyFocus.getMemorization());
        entity.setFormalReasoning(studyFocus.getFormalReasoning());
        entity.setConceptualUnderstanding(studyFocus.getConceptualUnderstanding());
        entity.setProblemSolving(studyFocus.getProblemSolving());
    }
}
