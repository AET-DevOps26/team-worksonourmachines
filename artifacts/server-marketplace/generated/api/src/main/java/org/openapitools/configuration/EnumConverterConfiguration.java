package org.openapitools.configuration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceTutorSort;
import org.openapitools.model.SharedMarketplaceWeekday;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * This class provides Spring Converter beans for the enum models in the OpenAPI specification.
 *
 * By default, Spring only converts primitive types to enums using Enum::valueOf, which can prevent
 * correct conversion if the OpenAPI specification is using an `enumPropertyNaming` other than
 * `original` or the specification has an integer enum.
 */
@Configuration(value = "org.openapitools.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.sharedMarketplaceApplicationStatusConverter")
    Converter<String, SharedMarketplaceApplicationStatus> sharedMarketplaceApplicationStatusConverter() {
        return new Converter<String, SharedMarketplaceApplicationStatus>() {
            @Override
            public SharedMarketplaceApplicationStatus convert(String source) {
                return SharedMarketplaceApplicationStatus.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.sharedMarketplaceLocationConverter")
    Converter<String, SharedMarketplaceLocation> sharedMarketplaceLocationConverter() {
        return new Converter<String, SharedMarketplaceLocation>() {
            @Override
            public SharedMarketplaceLocation convert(String source) {
                return SharedMarketplaceLocation.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.sharedMarketplaceTutorSortConverter")
    Converter<String, SharedMarketplaceTutorSort> sharedMarketplaceTutorSortConverter() {
        return new Converter<String, SharedMarketplaceTutorSort>() {
            @Override
            public SharedMarketplaceTutorSort convert(String source) {
                return SharedMarketplaceTutorSort.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.sharedMarketplaceWeekdayConverter")
    Converter<String, SharedMarketplaceWeekday> sharedMarketplaceWeekdayConverter() {
        return new Converter<String, SharedMarketplaceWeekday>() {
            @Override
            public SharedMarketplaceWeekday convert(String source) {
                return SharedMarketplaceWeekday.fromValue(source);
            }
        };
    }

}
