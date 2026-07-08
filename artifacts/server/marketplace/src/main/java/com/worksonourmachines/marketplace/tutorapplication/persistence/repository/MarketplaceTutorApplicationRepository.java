package com.worksonourmachines.marketplace.tutorapplication.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationEntity;
import com.worksonourmachines.marketplace.tutorapplication.persistence.entity.MarketplaceTutorApplicationStatus;

public interface MarketplaceTutorApplicationRepository extends JpaRepository<MarketplaceTutorApplicationEntity, UUID> {

    @EntityGraph(attributePaths = "module")
    List<MarketplaceTutorApplicationEntity> findAllByOrderBySubmittedAtDesc();

    @EntityGraph(attributePaths = "module")
    List<MarketplaceTutorApplicationEntity> findByStatusOrderBySubmittedAtDesc(
            MarketplaceTutorApplicationStatus status);
}
