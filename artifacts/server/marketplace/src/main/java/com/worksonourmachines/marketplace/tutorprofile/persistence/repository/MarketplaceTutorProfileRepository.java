package com.worksonourmachines.marketplace.tutorprofile.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksonourmachines.marketplace.tutorprofile.persistence.entity.MarketplaceTutorProfileEntity;

public interface MarketplaceTutorProfileRepository extends JpaRepository<MarketplaceTutorProfileEntity, UUID> {

    Optional<MarketplaceTutorProfileEntity> findByUserId(UUID userId);

    Optional<MarketplaceTutorProfileEntity> findByIdAndPublishedTrue(UUID id);

    List<MarketplaceTutorProfileEntity> findByPublishedTrueOrderByDisplayNameAsc();
}
