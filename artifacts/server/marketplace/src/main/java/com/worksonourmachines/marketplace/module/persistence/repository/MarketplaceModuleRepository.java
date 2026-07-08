package com.worksonourmachines.marketplace.module.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;

public interface MarketplaceModuleRepository extends JpaRepository<MarketplaceModuleEntity, UUID> {

    @EntityGraph(attributePaths = "topics")
    List<MarketplaceModuleEntity> findAllByOrderByCodeAsc();

    Optional<MarketplaceModuleEntity> findByCodeIgnoreCase(String code);
}
