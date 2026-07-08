package com.worksonourmachines.student.profile.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksonourmachines.student.profile.persistence.entity.StudentProfileEntity;

import java.util.UUID;

public interface StudentProfileRepository extends JpaRepository<StudentProfileEntity, UUID> {
}
