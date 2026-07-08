package com.worksonourmachines.student.profile.service;

import java.util.UUID;

import org.openapitools.model.SharedStudentStudentProfile;
import org.openapitools.model.SharedStudentStudentProfileInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worksonourmachines.server.common.security.AuthenticatedUser;
import com.worksonourmachines.student.profile.mapper.StudentProfileMapper;
import com.worksonourmachines.student.profile.persistence.entity.StudentProfileEntity;
import com.worksonourmachines.student.profile.persistence.repository.StudentProfileRepository;

@Service
public class StudentProfileService {

    private final AuthenticatedUser authenticatedUser;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentProfileMapper studentProfileMapper;

    public StudentProfileService(
            AuthenticatedUser authenticatedUser,
            StudentProfileRepository studentProfileRepository,
            StudentProfileMapper studentProfileMapper) {
        this.authenticatedUser = authenticatedUser;
        this.studentProfileRepository = studentProfileRepository;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Transactional(readOnly = true)
    public SharedStudentStudentProfile getCurrentStudentProfile() {
        UUID studentId = this.authenticatedUser.id();
        return this.studentProfileRepository.findById(studentId)
                .map(this.studentProfileMapper::toDto)
                .orElseGet(() -> this.studentProfileMapper.defaultDto(this.authenticatedUser.getUsername()));
    }

    @Transactional
    public SharedStudentStudentProfile updateCurrentStudentProfile(SharedStudentStudentProfileInput input) {
        if (input.getBio().isBlank() || input.getDisplayName().isBlank() || input.getLanguages().isEmpty()) {

        }

        UUID studentId = this.authenticatedUser.id();
        StudentProfileEntity profile = this.studentProfileRepository.findById(studentId)
                .orElseGet(() -> new StudentProfileEntity(studentId));
        this.studentProfileMapper.updateEntity(profile, input);
        return this.studentProfileMapper.toDto(this.studentProfileRepository.save(profile));
    }
}
