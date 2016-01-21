package com.neoteric.starter.http.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.neoteric.starter.Constants.LOG_PREFIX;

@Configuration
@ConditionalOnClass(Feign.class)
@EnableConfigurationProperties(CustomFeignProperties.class)
public class FeignJaxRsAutoConfiguration {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FeignJaxRsAutoConfiguration.class);

    @Autowired
    CustomFeignProperties feignProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Contract feignContract() {
        return new JAXRSContract();
    }

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder(objectMapper);
    }

    @Bean
    public RequestIdAppendInterceptor idAppendInterceptor() {
        return new RequestIdAppendInterceptor();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        LOG.debug("{}Feign Logger level: {}", LOG_PREFIX, feignProperties.getLoggerLevel());
        return feignProperties.getLoggerLevel();
    }
}
