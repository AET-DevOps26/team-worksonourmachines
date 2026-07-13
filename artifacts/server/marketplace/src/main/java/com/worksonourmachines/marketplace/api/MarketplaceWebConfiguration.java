package com.worksonourmachines.marketplace.api;

import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.SharedMarketplaceWeekday;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class MarketplaceWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(
                String.class,
                SharedMarketplaceApplicationStatus.class,
                SharedMarketplaceApplicationStatus::fromValue);
        registry.addConverter(
                String.class,
                SharedMarketplaceLocation.class,
                SharedMarketplaceLocation::fromValue);
        registry.addConverter(
                String.class,
                SharedMarketplaceTutorSort.class,
                SharedMarketplaceTutorSort::fromValue);
        registry.addConverter(
                String.class,
                SharedMarketplaceWeekday.class,
                SharedMarketplaceWeekday::fromValue);
    }
}
