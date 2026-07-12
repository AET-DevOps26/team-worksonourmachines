package com.worksonourmachines.student.goal.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.worksonourmachines.student.goal.persistence.entity.LearningGoalEntity;

public interface LearningGoalRepository extends JpaRepository<LearningGoalEntity, UUID> {

    @EntityGraph(attributePaths = "locations")
    Optional<LearningGoalEntity> findByIdAndStudentId(UUID id, UUID studentId);

    @EntityGraph(attributePaths = "locations")
    List<LearningGoalEntity> findAllByStudentIdOrderByTargetDateAscIdAsc(UUID studentId);
}
