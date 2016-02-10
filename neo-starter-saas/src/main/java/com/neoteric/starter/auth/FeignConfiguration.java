package com.neoteric.starter.auth;

import feign.Contract;
import feign.jaxrs.JAXRSContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public Contract feignContract() {
        return new JAXRSContract();
    }
}