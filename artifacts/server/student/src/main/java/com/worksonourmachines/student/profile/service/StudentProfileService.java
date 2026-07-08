package com.worksonourmachines.student.profile.service;

import org.openapitools.model.SharedStudentStudentProfile;
import org.openapitools.model.SharedStudentStudentProfileInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worksonourmachines.student.profile.mapper.StudentProfileMapper;
import com.worksonourmachines.student.profile.persistence.entity.StudentProfileEntity;
import com.worksonourmachines.student.profile.persistence.repository.StudentProfileRepository;

import java.util.UUID;

@Service
public class StudentProfileService {

    private static final UUID CURRENT_STUDENT_ID = UUID.randomUUID();

    private final StudentProfileRepository studentProfileRepository;
    private final StudentProfileMapper studentProfileMapper;

    public StudentProfileService(
            StudentProfileRepository studentProfileRepository,
            StudentProfileMapper studentProfileMapper) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Transactional(readOnly = true)
    public SharedStudentStudentProfile getCurrentStudentProfile() {
        return studentProfileRepository.findById(CURRENT_STUDENT_ID)
                .map(studentProfileMapper::toDto)
                .orElseGet(studentProfileMapper::defaultDto);
    }

    @Transactional
    public SharedStudentStudentProfile updateCurrentStudentProfile(SharedStudentStudentProfileInput input) {
        StudentProfileEntity profile = studentProfileRepository.findById(CURRENT_STUDENT_ID)
                .orElseGet(() -> new StudentProfileEntity(CURRENT_STUDENT_ID));
        studentProfileMapper.updateEntity(profile, input);
        return studentProfileMapper.toDto(studentProfileRepository.save(profile));
    }
}
