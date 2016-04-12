package com.neoteric.starter.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StarterMvcAutoConfiguration {

    @Bean
    ClassNameWithRequestMappingHandlerMapping mapping() {
        ClassNameWithRequestMappingHandlerMapping mapping = new ClassNameWithRequestMappingHandlerMapping();
        mapping.setInitialPrefix("api");
        mapping.setClassSuffix("API");
        mapping.setOrder(-10);
        return mapping;
    }
}
