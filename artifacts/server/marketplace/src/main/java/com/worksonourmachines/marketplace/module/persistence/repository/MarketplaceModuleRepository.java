package com.worksonourmachines.marketplace.module.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;

public interface MarketplaceModuleRepository extends JpaRepository<MarketplaceModuleEntity, UUID> {

    @EntityGraph(attributePaths = "topics")
    List<MarketplaceModuleEntity> findAllByOrderByCodeAsc();

    @Query("""
            SELECT module
            FROM MarketplaceModuleEntity module
            WHERE :query IS NULL
                OR LOWER(module.code) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(module.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(module.description) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<MarketplaceModuleEntity> findAllMatching(@Param("query") String query, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

    @EntityGraph(attributePaths = "topics")
    Optional<MarketplaceModuleEntity> findByCodeIgnoreCase(String code);
}
