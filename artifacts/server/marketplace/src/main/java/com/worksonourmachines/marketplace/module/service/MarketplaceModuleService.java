package com.worksonourmachines.marketplace.module.service;

import java.util.List;

import org.openapitools.model.SharedMarketplaceAdminModuleInput;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.module.mapper.MarketplaceModuleMapper;
import com.worksonourmachines.marketplace.module.persistence.repository.MarketplaceModuleRepository;

@Service
public class MarketplaceModuleService {

    private final MarketplaceModuleRepository marketplaceModuleRepository;
    private final MarketplaceModuleMapper marketplaceModuleMapper;

    public MarketplaceModuleService(
            MarketplaceModuleRepository marketplaceModuleRepository,
            MarketplaceModuleMapper marketplaceModuleMapper) {
        this.marketplaceModuleRepository = marketplaceModuleRepository;
        this.marketplaceModuleMapper = marketplaceModuleMapper;
    }

    @Transactional(readOnly = true)
    public List<SharedMarketplaceModuleDetail> listAdminModules() {
        return marketplaceModuleMapper.toDetails(marketplaceModuleRepository.findAllByOrderByCodeAsc());
    }

    @Transactional
    public SharedMarketplaceModuleDetail createAdminModule(SharedMarketplaceAdminModuleInput input) {
        if (marketplaceModuleRepository.existsByCodeIgnoreCase(input.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Module code already exists.");
        }
        return marketplaceModuleMapper.toDetail(
                marketplaceModuleRepository.save(marketplaceModuleMapper.toCreateEntity(input)));
    }
}
