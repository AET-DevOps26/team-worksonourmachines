package com.worksonourmachines.marketplace.module.service;

import java.util.List;

import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
