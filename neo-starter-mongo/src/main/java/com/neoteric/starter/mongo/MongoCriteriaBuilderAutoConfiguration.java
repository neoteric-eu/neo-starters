package com.neoteric.starter.mongo;

import com.neoteric.starter.mongo.request.RequestParamsCriteriaBuilder;
import com.neoteric.starter.mongo.request.processors.MongoRequestFieldProcessor;
import com.neoteric.starter.mongo.request.processors.MongoRequestLogicalOperatorProcessor;
import com.neoteric.starter.mongo.request.processors.fields.MongoFieldToLogicalOperatorSubProcessor;
import com.neoteric.starter.mongo.request.processors.fields.MongoFieldToOperatorSubProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class MongoCriteriaBuilderAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MongoCriteriaBuilderAutoConfiguration.class);

    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @Bean
    public RequestParamsCriteriaBuilder requestParamsCriteriaBuilder() {
        LOG.debug("{}Registering RequestParamsCriteriaBuilder.", StarterMongoConstants.LOG_PREFIX);
        return new RequestParamsCriteriaBuilder();
    }

    @Bean
    public MongoRequestLogicalOperatorProcessor mongoRequestLogicalOperatorProcessor() {
        return new MongoRequestLogicalOperatorProcessor();
    }

    @Bean
    public MongoRequestFieldProcessor mongoRequestFieldProcessor() {
        return new MongoRequestFieldProcessor();
    }

    @Bean
    public MongoFieldToOperatorSubProcessor mongoFieldToOperatorSubProcessor() {
        DateTimeFormatter dateTimeFormatterWithZone = dateTimeFormatter.getZone() == null
                ? dateTimeFormatter.withZone(ZoneId.systemDefault())
                : dateTimeFormatter;
        return new MongoFieldToOperatorSubProcessor(dateTimeFormatterWithZone);
    }

    @Bean
    public MongoFieldToLogicalOperatorSubProcessor mongoFieldToLogicalOperatorSubProcessor() {
        return new MongoFieldToLogicalOperatorSubProcessor();
    }
}
