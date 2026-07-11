package com.worksonourmachines.marketplace.module.service;

import java.util.List;

import org.openapitools.model.ModulePage;
import org.openapitools.model.SharedMarketplaceAdminModuleInput;
import org.openapitools.model.SharedMarketplaceAdminModuleUpdateInput;
import org.openapitools.model.SharedMarketplaceModuleDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.marketplace.module.mapper.MarketplaceModuleMapper;
import com.worksonourmachines.marketplace.module.persistence.entity.MarketplaceModuleEntity;
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

    @Transactional(readOnly = true)
    public ModulePage listModules(Integer page, Integer pageSize, @Nullable String q) {
        String normalizedQuery = normalizeSearchQuery(q);
        int resolvedPage = page == null ? 1 : page;
        int resolvedPageSize = pageSize == null ? 20 : pageSize;
        PageRequest pageable = PageRequest.of(resolvedPage - 1, resolvedPageSize, Sort.by("code").ascending());

        Page<MarketplaceModuleEntity> modulesPage = normalizedQuery == null
                ? marketplaceModuleRepository.findAll(pageable)
                : marketplaceModuleRepository.findAllMatching(normalizedQuery, pageable);

        return marketplaceModuleMapper.toPage(modulesPage, resolvedPage, resolvedPageSize);
    }

    @Transactional(readOnly = true)
    public SharedMarketplaceModuleDetail getModule(String code) {
        return marketplaceModuleMapper.toDetail(findModuleByCode(code));
    }

    @Transactional
    public SharedMarketplaceModuleDetail createAdminModule(SharedMarketplaceAdminModuleInput input) {
        if (marketplaceModuleRepository.existsByCodeIgnoreCase(input.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Module code already exists.");
        }
        return marketplaceModuleMapper.toDetail(
                marketplaceModuleRepository.save(marketplaceModuleMapper.toCreateEntity(input)));
    }

    @Transactional
    public SharedMarketplaceModuleDetail updateAdminModule(
            String code,
            SharedMarketplaceAdminModuleUpdateInput input) {
        var module = findModuleByCode(code);
        marketplaceModuleMapper.updateEntity(module, input);
        return marketplaceModuleMapper.toDetail(marketplaceModuleRepository.save(module));
    }

    private MarketplaceModuleEntity findModuleByCode(String code) {
        return marketplaceModuleRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found."));
    }

    private static String normalizeSearchQuery(@Nullable String q) {
        if (q == null || q.isBlank()) {
            return null;
        }
        return q.trim();
    }
}
