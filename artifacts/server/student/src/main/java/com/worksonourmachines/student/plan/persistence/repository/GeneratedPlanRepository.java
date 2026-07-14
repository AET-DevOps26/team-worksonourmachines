package com.worksonourmachines.student.plan.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.worksonourmachines.student.plan.persistence.entity.GeneratedPlanEntity;

public interface GeneratedPlanRepository extends JpaRepository<GeneratedPlanEntity, UUID> {

    Optional<GeneratedPlanEntity> findByGoalId(UUID goalId);

    @Transactional
    void deleteByGoalId(UUID goalId);
}
