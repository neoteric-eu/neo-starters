package com.neoteric.starter.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.Constants;
import com.neoteric.starter.jackson.rison.RisonFactory;
import com.neoteric.starter.jersey.JerseyDefaultsAutoConfiguration;
import com.neoteric.starter.request.params.RequestParametersFilter;
import com.neoteric.starter.request.tracing.RequestIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RequestProperties.class, JerseyProperties.class})
@AutoConfigureAfter({JacksonAutoConfiguration.class, JerseyDefaultsAutoConfiguration.class})
public class RequestAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RequestAutoConfiguration.class);

    @Autowired
    RequestProperties requestProperties;

    @Autowired
    JerseyProperties jerseyProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    FilterRegistrationBean registerRequestIdFilter() {
        return new FilterRegistrationBean(new RequestIdFilter(jerseyProperties.getApplicationPath()));
    }

    @Bean //TODO: Check if Rison is worth supporting
    FilterRegistrationBean registerRequestParamsFilter() throws Exception {
        RequestProperties.FiltersFormat filtersFormat = requestProperties.getFiltersFormat();
        ObjectMapper requestMapper;
        switch(filtersFormat) {
            case RISON:
                LOG.trace("{}Picked RISON Mapper", Constants.LOG_PREFIX);
                requestMapper = new ObjectMapper(new RisonFactory());
                break;
            case JSON:
                LOG.trace("{}Picked default JSON Mapper", Constants.LOG_PREFIX);
                requestMapper = objectMapper;
                break;
            default:
                LOG.error("{}{} format not supported.", Constants.LOG_PREFIX, filtersFormat);
                throw new Exception(filtersFormat + " not supported.");
        }

        return new FilterRegistrationBean(new RequestParametersFilter(requestMapper, jerseyProperties.getApplicationPath()));
    }
}